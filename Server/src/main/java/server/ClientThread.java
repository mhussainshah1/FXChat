package server;

import common.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

// One instance of this thread will run for each client
class ClientThread extends Thread {
    private Server server;
    // the socket to get messages from client
    Socket socket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    // my unique id (easier for deconnection)
    int id;
    // the Username of the Client
    String username;
    // message object to receive message and its type
    ChatMessage cm;
    // timestamp
    String date;

    // Constructor
    ClientThread(Server server, Socket socket) {
        this.server = server;

        // a unique id
        id = ++Server.uniqueId;
        this.socket = socket;

        //Creating both Data Stream
        server.displayWithoutStamp("Thread trying to create Object Input/Output Streams");
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());
            // read the username
            username = (String) sInput.readObject();
            server.broadcast(server.notif + username + " has joined the chat room." + server.notif);
        } catch (IOException e) {
            server.display("Exception creating new Input/output Streams: " + e);
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        date = new Date() + "\n";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // infinite loop to read and forward message
    public void run() {
        // to loop until LOGOUT
        boolean keepGoing = true;
        while (keepGoing) {
            // read a String (which is an object)
            try {
                cm = (ChatMessage) sInput.readObject();
            } catch (IOException e) {
                server.display(username + " Exception reading Streams: " + e);
                break;
            } catch (ClassNotFoundException e2) {
                break;
            }
            // get the message from the common.ChatMessage object received
            String message = cm.getMessage();

            // different actions based on type message
            switch (cm.getType()) {
                case ChatMessage.MESSAGE:
                    boolean confirmation = server.broadcast(username + ": " + message);
                    if (!confirmation) {
                        String msg = server.notif + "Sorry. No such user exists." + server.notif;
                        writeMsg(msg);
                    }
                    break;
                case ChatMessage.LOGOUT:
                    server.display(username + " disconnected with a LOGOUT message.");
                    keepGoing = false;
                    break;
                case ChatMessage.LIST:
                    writeMsg("List of the users connected at " + server.sdf.format(new Date()) + "\n");
                    // send list of active clients
                    for (int i = 0; i < server.threadList.size(); ++i) {
                        ClientThread ct = server.threadList.get(i);
                        writeMsg((i + 1) + ") " + ct.username + " since " + ct.date);
                    }
                    break;
            }
        }
        // if out of the loop then disconnected and remove from client list
        server.remove(id);
        close();
    }

    // close everything
    private void close() {
        try {
            if (sOutput != null) sOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (sInput != null) sInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // write a String to the client.Client output stream
    public boolean writeMsg(String msg) {
        // if client.Client is still connected send the message to it
        if (!socket.isConnected()) {
            close();
            return false;
        }
        // write the message to the stream
        try {
            sOutput.writeObject(msg);
        }
        // if an error occurs, do not abort just inform the user
        catch (IOException e) {
            server.display(server.notif + "Error sending message to " + username + server.notif);
            server.display(e.toString());
            e.printStackTrace();
        }
        return true;
    }
}