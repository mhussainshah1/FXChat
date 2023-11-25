package com.server;

import com.common.Data;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

public class ChatServer {
    private final BiConsumer<Serializable, Integer> onReceiveCallback;
    private Thread thread;
    private final ServerSocket serverSocket;
    private Socket socket;
    private ClientHandler clientHandler;
    private final String roomList;
    private final ExecutorService pool;

    public ChatServer(String serverName, int port, int maximumGuestNumber, BiConsumer<Serializable, Integer> onReceiveCallback) throws IOException {
        try (Data data = new Data("server.properties")) {
            data.setServerName(serverName);
            data.setServerPort(port);
            data.setMaximumGuestNumber(maximumGuestNumber);
            roomList = data.getRoomList();
            pool = Executors.newFixedThreadPool(maximumGuestNumber);
        }
        this.serverSocket = new ServerSocket(port);
        this.onReceiveCallback = onReceiveCallback;
    }

    public void startConnection() {
        thread = new Thread(this::readConnectionLoop);
        thread.start();
    }

    private void readConnectionLoop() {
        //Accepting all the client connections and create a separate thread
        while (!serverSocket.isClosed()) {
            try {
                //Accepting the Server Connections
                socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                // Create a Separate Thread for that each client
                clientHandler = new ClientHandler(this, socket, onReceiveCallback);
                pool.execute(clientHandler);
//                clientHandler.start();
            } catch (IOException e) {
                closeConnection();
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            pool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoomList() {
        return roomList;
    }

    public void broadcast(String message) {
        onReceiveCallback.accept("Server: " + message, MESSAGE_TYPE_ADMIN);
        if (clientHandler != null)
            clientHandler.broadcastMessage(message);
    }
}