package client;
import common.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//The client.Client that can be run as a console
public class ChatClient {

    private final int port;                    //port
    // notification
    private String notif = " *** ";
    private String server;
    private String userName;
    private String message;

    // for I/O
    private ObjectInputStream sInput;        // to read from the socket
    private ObjectOutputStream sOutput;        // to write on the socket
    private Socket socket;                    // socket object

    /**
     * Constructor to set below things main.java.server: the main.java.server address  port: the port number username: the username
     */
    public ChatClient(String server, int port, String userName) {
        this.server = server;
        this.port = port;
        this.userName = userName;
        // try to connect to the main.java.server and return if not connected
        if (!start())
            return;

        // infinite loop to get the input from the user
        while (true) {
            String msg = getMessage();

            // logout if message is LOGOUT
            if (msg.equalsIgnoreCase("LOGOUT")) {
                sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                break;
            }
            // message to check who are present in chatroom
            else if (msg.equalsIgnoreCase("WHOISIN")) {
                sendMessage(new ChatMessage(ChatMessage.LIST, ""));
            }
            // regular text message
            else {
                sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
            }
        }

        // client completed its job. disconnect client.
        disconnect();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 1500,  "Amir");
        client.setMessage("Test");
    }

    /*
     * To start the chat
     */
    public boolean start() {
        // try to connect to the main.java.server
        try {
            socket = new Socket(server, port);
        }
        // exception handler if it failed
        catch (Exception ec) {
            display("Error connecting to main.java.server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the main.java.server
        new ListenFromServer().start();
        // Send our username to the main.java.server this is the only message that we
        // will send as a String. All other messages will be common.ChatMessage objects
        try {
            sOutput.writeObject(userName);
        } catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console
     */
    private void display(String msg) {
        System.out.println(msg);
    }

    /*
     * To send a message to the main.java.server
     */
    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to main.java.server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect
     */
    private void disconnect() {
        try {
            if (sInput != null)
                sInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (sOutput != null)
                sOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * a class that waits for the message from the main.java.server
     */
    class ListenFromServer extends Thread {
        public void run() {
            while (true) {
                try {
                    // read the message form the input datastream
                    String msg = (String) sInput.readObject();
                    // print the message
                    System.out.println(msg);
                    System.out.print("> ");
                } catch (IOException e) {
                    display(notif + "Server has closed the connection: " + e + notif);
                    break;
                } catch (ClassNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}