package com.server;

import com.common.Data;
import com.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

// the server that can be run as a console
public class Server extends ServerNetWorkConnection implements Serializable {
    public static int uniqueId;// a unique ID for each connection
    public final List<ClientThread> clientThreads;// an ArrayList to keep the list of the client.Client
    public final SimpleDateFormat sdf; // to display time
    public final String notif = " *** ";// notification
    private final int port;// the port number to listen for connection
    private final ConnectionThread connectionThread = new ConnectionThread();
    private final Consumer<Serializable> onReceiveCallback;
    private boolean keepGoing;// to check if server is running
    private static ExecutorService pool;

    //constructor that receive the port to listen to for connection as parameter
    public Server(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.onReceiveCallback = onReceiveCallback;
        this.port = port;// the port
        sdf = new SimpleDateFormat("HH:mm:ss");// to display hh:mm:ss
        clientThreads = new ArrayList<>();// an ArrayList to keep the list of the Client
        try(Data data = new Data("server.properties");) {
            pool = Executors.newFixedThreadPool(data.getMaximumGuestNumber());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startConnection() throws IOException {
        connectionThread.start();
    }

    public void closeConnection() throws IOException {// to stop the server
        keepGoing = false;
        connectionThread.closeConnection();
    }

    // to broadcast a message to all Clients
    public synchronized boolean broadcast(String message) {
        // add timestamp to the message
        String time = sdf.format(new Date());

        // to check if message is private i.e. client to client message
        String[] w = message.split(" ", 3);

        boolean isPrivate = w[1].charAt(0) == '@';

        // if private message, send message to mentioned username only
        if (isPrivate) {
            String toCheck = w[1].substring(1);

            message = w[0] + w[2];
            String messageLf = time + "  " + message + "\n";
            boolean found = false;
            // we loop in reverse order to find the mentioned username
            for (int y = clientThreads.size(); --y >= 0; ) {
                ClientThread ct1 = clientThreads.get(y);
                String check = ct1.getUserName();
                if (check.equals(toCheck)) {
                    // try to write to the client.Client if it fails remove it from the list
                    if (!ct1.writeMsg(messageLf)) {
                        clientThreads.remove(y);
                        display("Disconnected Client " + ct1.userName + " removed from list.");
                    }
                    // username found and delivered the message
                    found = true;
                    break;
                }
            }
            // mentioned user not found, return false
            return found;
        }
        // if message is a broadcast message
        else {
            String messageLf = time + " " + message + "\n";
            // display message
            onReceiveCallback.accept(messageLf);

            // we loop in reverse order in case we would have to remove a client.Client
            // because it has disconnected
            for (int i = clientThreads.size(); --i >= 0; ) {
                ClientThread ct = clientThreads.get(i);
                // try to write to the client.Client if it fails remove it from the list
                if (!ct.writeMsg(messageLf)) {
                    clientThreads.remove(i);
                    display("Disconnected client.Client " + ct.userName + " removed from list.");
                }
            }
        }
        return true;
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        onReceiveCallback.accept(time);
    }

    // Display an event to the console
    public void send(Serializable data) throws IOException {
        for (ClientThread cl : clientThreads) {
            cl.out.writeObject(data);
        }
    }

    // if client sent LOGOUT message to exit
    synchronized void remove(int id) {
        String disconnectedClient = "";
        // scan the array list until we found the Id
        for (ClientThread clientThread : clientThreads) {
            if (clientThread.id == id) {// if found remove it
                disconnectedClient = clientThread.getUserName();
                clientThreads.remove(clientThread);
                break;
            }
        }
        broadcast(notif + disconnectedClient + " has left the chat room." + notif);
    }

    @Override
    protected String getIP() {
        return "localhost";
    }

    @Override
    protected int getPort() {
        return port;
    }

    private class ConnectionThread extends Thread {
        ServerSocket serverSocket;
        public Socket socket;

        @Override
        public synchronized void run() {
            keepGoing = true;
            //create socket server and wait for connection requests
            try (ServerSocket serverSocket = new ServerSocket(port);) {// the socket used by the server
                this.serverSocket = serverSocket;
                while (keepGoing) { // infinite loop to wait for connections ( till server is active )
                    display("Server waiting for Clients on port " + port + ".");
                    this.socket = serverSocket.accept(); // accept connection if requested from client
                    if (!keepGoing)
                        break; // break if server stopped

                    ClientThread t = new ClientThread(socket);// if client is connected, create its thread
                    clientThreads.add(t);//add this client to arraylist
                    pool.execute(t);
                    //t.start();
                }
            } catch (IOException e) {
                display(sdf.format(new Date()) + " Exception on new ServerSocket: " + e);
            }
            try {
                closeConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void closeConnection() throws IOException {// try to stop the server
            serverSocket.close();
            pool.shutdown();
//            for (ClientThread tc : clientThreads) {// close all data streams and socket
//                tc.close();
//            }
        }
    }

    // One instance of this thread will run for each client
    private class ClientThread extends Thread {
        private int id; // my unique id (easier for disconnection)
        private String date;// timestamp
        private Socket socket; // the socket to get messages from client
        private String userName;// the Username of the Client
        private Message message;// message object to receive message and its type
        ObjectInputStream in;
        ObjectOutputStream out;

        // Constructor
        ClientThread(Socket socket) {
            // a unique id
            id = ++Server.uniqueId;
            this.socket = socket;
            date = new Date() + "\n";
        }

        public String getUserName() {
            return userName;
        }

        // infinite loop to read and forward message
        @Override
        public void run() {
            //Creating both Data Stream
            onReceiveCallback.accept("Thread trying to create Object Input/Output Streams\n");
            try (var out = new ObjectOutputStream(socket.getOutputStream());
                 var in = new ObjectInputStream(socket.getInputStream());) {

                this.out = out;
                this.in = in;

                userName = (String) in.readObject();// read the username
                broadcast(notif + userName + " has joined the chat room." + notif);
                // to loop until LOGOUT
                boolean keepGoing = true;
                while (keepGoing) {
                    message = (Message) in.readObject();
                    // get the message from the common.ChatMessage object received
                    String message = this.message.getMessage();

                    // different actions based on type message
                    switch (this.message.getMessageType()) {
                        case Message.MESSAGE:
                            boolean confirmation = broadcast(userName + ": " + message);
                            if (!confirmation) {
                                writeMsg(notif + "Sorry. No such user exists." + notif + "\n");
                            }
                            break;
                        case Message.LOGOUT:
                            display(userName + " disconnected with a LOGOUT message.");
                            keepGoing = false;
                            break;
                        case Message.LIST:
                            writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
                            // send list of active clients
                            for (int i = 0; i < clientThreads.size(); ++i) {
                                ClientThread ct = clientThreads.get(i);
                                writeMsg((i + 1) + ") " + ct.userName + " since " + ct.date);
                            }
                            break;
                    }
                }
                // if out of the loop then disconnected and remove from client list

            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            remove(id);
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // close everything
        private void close() throws IOException {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        }

        // write a String to the client.Client output stream
        public boolean writeMsg(String msg) {
            try {
                // if client.Client is still connected send the message to it
                if (!socket.isConnected()) {
                    close();
                    return false;
                }
                out.writeObject(msg);// write the message to the stream
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                display(notif + "Error sending message to " + userName + notif);
                display(e.toString());
            }
            return true;
        }
    }
}