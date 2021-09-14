package client;

import common.ChatMessage;
import controller.ChatClientController;
import javafx.application.Platform;
import server.MessageObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import static client.MessageObject.MESSAGE_TYPE_ADMIN;

//The client.Client that can be run as a console
public class ChatClient {

    // to display time
    private SimpleDateFormat sdf;
    private ChatClientController chatClientController;
    private MessageObject messageObject;

    // notification
    private String notif = " *** ";
    private String message;

    // for I/O
    private ObjectInputStream sInput;           // to read from the socket
    private ObjectOutputStream sOutput;         // to write on the socket
    private Socket socket;                      // socket object

    private String server;
    private String userName;
    private int port;                           //port

    /**
     * Constructor to set below things main.java.server: the main.java.server address  port: the port number username: the username
     */
    public ChatClient(String server, int port, String userName, ChatClientController chatClientController) {
        this.server = server;
        this.port = port;
        this.userName = userName;
        this.chatClientController = chatClientController;

        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");

        display("server name = " + server +
                ",port number = " + port +
                ",user name = " + userName);

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
    public void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        System.out.print(time);

        Platform.runLater(() -> {
            messageObject = new MessageObject();
            chatClientController.getMessageBoard().getChildren().add(messageObject.getText(time, MESSAGE_TYPE_ADMIN));
        });
    }

    /*
     * To send a message to the main.java.server
     */
    public void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
            display(msg.getMessage());
        } catch (IOException e) {
            display("Exception writing to main.java.server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect
     */
    public void disconnect() {
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
                    display(msg);
                    display("> ");
                } catch (IOException e) {
                    display(notif + "Server has closed the connection: " + e + notif);
                    break;
                } catch (ClassNotFoundException e2) {
                    display(e2.getMessage());
                    e2.printStackTrace();
                }
            }
        }
    }
}