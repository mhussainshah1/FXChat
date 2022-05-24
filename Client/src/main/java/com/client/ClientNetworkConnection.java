package com.client;

import com.common.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

import static com.controller.ClientController.QUIT_TYPE_DEFAULT;
import static com.controller.ClientController.QUIT_TYPE_KICK;

public abstract class ClientNetworkConnection {
    private final String notif = " *** ";// notification

    private final ListenFromServer connectionThread = new ListenFromServer();
    private final Consumer<Serializable> onReceiveCallback;

    public ClientNetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
    }

    public void startConnection() throws IOException {
        connectionThread.start();
    }

    public void send(Serializable data) throws IOException {
        connectionThread.out.writeObject(data);
    }

    public void closeConnection(int quitType) throws Exception {
        connectionThread.quitConnection(quitType);
    }

    public void disconnectChat() {
        connectionThread.quitConnection(QUIT_TYPE_DEFAULT);
    }

    protected abstract String getIP();

    protected abstract int getPort();

    protected abstract String getUserName();

    /**
     * a class that waits for the message from the server
     */
    class ListenFromServer extends Thread {
        // for I/O
        private Socket socket;               // socket object
        private ObjectOutputStream out;      // to write on the socket
        private ObjectInputStream in;        // to read from the socket

        @Override
        public void run() {
            try (var socket = new Socket(getIP(), getPort());
                 var in = new ObjectInputStream(socket.getInputStream());
                 var out = new ObjectOutputStream(socket.getOutputStream())) {

                this.socket = socket;
                this.out = out;
                this.in = in;

                socket.setTcpNoDelay(true);//disable buffering - send message quicker without waiting for buffer to get full
                onReceiveCallback.accept("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort() + "\n");
                out.writeObject(getUserName());//send username to the server

                while (true) {
                    // read the message form the input datastream
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallback.accept(data);
                }
            } catch (Exception e) {
                onReceiveCallback.accept(notif + "Server has closed the connection: " + e + notif);
            }
        }

        public void quitConnection(int quitType) {
            if (socket != null) {
                onReceiveCallback.accept("CONNECTION TO THE SERVER CLOSED\n");
                try {
                    if (quitType == QUIT_TYPE_DEFAULT)
                        send(new ChatMessage(ChatMessage.REMOVE, ""));
                    if (quitType == QUIT_TYPE_KICK)
                        send(new ChatMessage(ChatMessage.KICKED_OUT, ""));
                    socket.close();
//                socket = null;
//                tappanel.UserCanvas.ClearAll();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
