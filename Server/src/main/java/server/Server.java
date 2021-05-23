package server;

import controller.ServerController;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static client.MessageObject.MESSAGE_TYPE_ADMIN;

// the server that can be run as a console
public class Server implements Runnable {
    // a unique ID for each connection
    public static int uniqueId;
    // an ArrayList to keep the list of the client.Client
    public final List<ClientThread> threadList;
    // to display time
    public final SimpleDateFormat sdf;
    // notification
    public final String notif = " *** ";
    // the port number to listen for connection
    private final int port;
    ServerController serverController;
    private Thread thread;
    private MessageObject messageObject;
    // to check if server is running
    private boolean keepGoing;
    //constructor that receive the port to listen to for connection as parameter

    public Server(int port, ServerController serverController) {
        // the port
        this.port = port;
        this.serverController = serverController;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // an ArrayList to keep the list of the client.Client
        threadList = new ArrayList<>();

        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        keepGoing = true;
        //create socket server and wait for connection requests
        try {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);
            while (keepGoing) { // infinite loop to wait for connections ( till server is active )
                display("Server waiting for Clients on port " + port + ".");
                Socket socket = serverSocket.accept(); // accept connection if requested from client
                if (!keepGoing)
                    break; // break if server stopped

                ClientThread t = new ClientThread(this, socket);// if client is connected, create its thread
                threadList.add(t);//add this client to arraylist
                t.start();
            }
            // try to stop the server
            try {
                serverSocket.close();
                for (ClientThread tc : threadList) {
                    try {// close all data streams and socket
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                        display(ioE.getMessage());
                        ioE.printStackTrace();
                    }
                }
            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    // to stop the server
    public void stop() {
        keepGoing = false;
        try {
            new Socket("localhost", port);
        } catch (Exception e) {
            display(e.getMessage());
            e.printStackTrace();
        }
    }

    // Display an event to the console
    public void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        System.out.print(time);

        Platform.runLater(() -> {
            messageObject = new MessageObject();
            serverController.getMessageBoard().getChildren().add(messageObject.getText(time, MESSAGE_TYPE_ADMIN));
        });
    }

    public void displayWithoutStamp(String message) {
        System.out.print(message + "\n");
        Platform.runLater(() -> {
            messageObject = new MessageObject();
            serverController.getMessageBoard().getChildren().add(messageObject.getText(message + "\n", MESSAGE_TYPE_ADMIN));
        });
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
            String tocheck = w[1].substring(1);

            message = w[0] + w[2];
            String messageLf = time + " " + message + "\n";
            boolean found = false;
            // we loop in reverse order to find the mentioned username
            for (int y = threadList.size(); --y >= 0; ) {
                ClientThread ct1 = threadList.get(y);
                String check = ct1.getUsername();
                if (check.equals(tocheck)) {
                    // try to write to the client.Client if it fails remove it from the list
                    if (!ct1.writeMsg(messageLf)) {
                        threadList.remove(y);
                        display("Disconnected Client " + ct1.username + " removed from list.");
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
            displayWithoutStamp(messageLf);
//            System.out.print(messageLf);

            // we loop in reverse order in case we would have to remove a client.Client
            // because it has disconnected
            for (int i = threadList.size(); --i >= 0; ) {
                ClientThread ct = threadList.get(i);
                // try to write to the client.Client if it fails remove it from the list
                if (!ct.writeMsg(messageLf)) {
                    threadList.remove(i);
                    display("Disconnected client.Client " + ct.username + " removed from list.");
                }
            }
        }
        return true;
    }

    // if client sent LOGOUT message to exit
    synchronized void remove(int id) {
        String disconnectedClient = "";
        // scan the array list until we found the Id
        for (int i = 0; i < threadList.size(); ++i) {
            ClientThread ct = threadList.get(i);
            // if found remove it
            if (ct.id == id) {
                disconnectedClient = ct.getUsername();
                threadList.remove(i);
                break;
            }
        }
        broadcast(notif + disconnectedClient + " has left the chat room." + notif);
    }
}