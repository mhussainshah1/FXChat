package com.server;

import com.service.DatabaseService;
import com.controller.tab.TabPaneManagerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
public class ChatServer {
    private String serverName;
    private int serverPort;
    private int maximumGuestNumber;
//    private BiConsumer<Serializable, Integer> tabPaneManagerController;
    private ServerSocket serverSocket;
    private String roomList;
    private ExecutorService pool;
    private Thread thread;
    private Socket socket;
    private ClientHandler clientHandler;
    private DatabaseService databaseService;
    private TabPaneManagerController tabPaneManagerController;
    @Autowired
    public ChatServer(DatabaseService databaseService, TabPaneManagerController tabPaneManagerController) throws IOException {
        serverName = "localhost";
        serverPort = 1436;
        maximumGuestNumber = 50;
        roomList = "General Teen Music Party";
        this.tabPaneManagerController = tabPaneManagerController;
        this.databaseService = databaseService;
        pool = Executors.newFixedThreadPool(maximumGuestNumber);
        this.serverSocket = new ServerSocket(serverPort);
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
                clientHandler = new ClientHandler(this, socket, tabPaneManagerController, databaseService);
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
        tabPaneManagerController.display("Server: " + message, MESSAGE_TYPE_ADMIN);
        if (clientHandler != null)
            clientHandler.broadcastMessage(message);
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getMaximumGuestNumber() {
        return maximumGuestNumber;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setMaximumGuestNumber(int maximumGuestNumber) {
        this.maximumGuestNumber = maximumGuestNumber;
    }
}