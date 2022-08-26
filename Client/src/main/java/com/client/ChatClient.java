package com.client;

import com.common.SocksSocket;
import com.common.SocksSocketImplFactory;
import com.controller.ClientController;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

import static com.common.CommonSettings.QUIT_TYPE_DEFAULT;
import static com.common.CommonSettings.QUIT_TYPE_KICK;

public class ChatClient {
    private final ClientController clientController;
    private final Consumer<Serializable> onReceiveCallback;
    private String userName;
    private String userRoom;
    private String serverName;
    private String proxyHost;
    private String serverData;
    private String roomList;
    private int serverPort, proxyPort;
    private boolean isProxy;
    private Socket socket;
    private BufferedReader bufferedIn;
    private OutputStream outputStream;

    public ChatClient(ClientController frame, String userName, String userRoom, String serverName, int serverPort, String proxyHost, int proxyPort, Consumer<Serializable> onReceiveCallback) {
        this.clientController = frame;
        this.userName = userName;
        this.userRoom = userRoom;
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.onReceiveCallback = onReceiveCallback;
    }

    public void startConnection() throws IOException {
        if (isProxy) {
            //Proxy
            SocksSocketImplFactory factory = new SocksSocketImplFactory(proxyHost, proxyPort);
            SocksSocket.setSocketImplFactory(factory);
            socket = new SocksSocket(serverName, serverPort);
            socket.setSoTimeout(0);
        } else {
            //Not Proxy
            socket = new Socket(serverName, serverPort);
        }
        outputStream = socket.getOutputStream();
        sendMessageToServer("HELO " + userName + " " + userRoom);
        bufferedIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Send HELO To Server
        startMessageReader();
    }

    private void startMessageReader() {
        Thread t = new Thread(this::readMessageLoop);
        t.start();
    }

    private void readMessageLoop() {
        try {
            while ((serverData = bufferedIn.readLine()) != null) {
                System.out.println(serverData);

                String[] tokens = serverData.split(" ");
                String command = tokens[0];

                Platform.runLater(() -> {
                    // RFC Coding
                    if (command.equalsIgnoreCase("LIST")) {
                        clientController.handleList(tokens);
                    }

                    // Room RFC
                    else if (command.equalsIgnoreCase("ROOM")) {
                        clientController.handleRoom(tokens);
                    }

                    // ADD RFC
                    else if (command.equalsIgnoreCase("ADD")) {
                        clientController.handleLogin(tokens);
                    }

                    // If Username Already Exists
                    else if (command.equalsIgnoreCase("EXIS")) {
                        clientController.handleUserExist();
                    }

                    // REMOVE User RFC Coding
                    else if (command.equalsIgnoreCase("REMO")) {
                        clientController.handleLoggOff(tokens);
                    }

                    // MESS RFC Coding Starts
                    else if (command.equalsIgnoreCase("MESS")) {
                        String[] tokensMsg = serverData.split(" ", 3);
                        clientController.handleMessage(tokensMsg);
                    }

                    // KICK RFC Starts
                    else if (command.equalsIgnoreCase("KICK")) {
                        clientController.handleKickUser();
                    }

                    // INKI RFC (Information about kicked off User
                    else if (command.equalsIgnoreCase("INKI")) {
                        clientController.handleKickUserInfo(tokens);
                    }

                    // Change Room RFC
                    else if (command.equalsIgnoreCase("CHRO")) {
                        userRoom = tokens[1];
                        clientController.handleChangeRoom(userRoom);
                    }

                    // Join Room RFC
                    else if (command.equalsIgnoreCase("JORO")) {
                        clientController.handleJoinRoom(tokens);
                    }

                    // Leave Room RFC
                    else if (command.equalsIgnoreCase("LERO")) {
                        clientController.handleLeaveRoom(tokens);
                    }

                    // Room Count RFC
                    else if (command.equalsIgnoreCase("ROCO")) {
                        clientController.handleRoomCount(tokens);
                    }

                    // Private Message RFC
                    else if (command.equalsIgnoreCase("PRIV")) {
                        String[] tokensMsg = serverData.split(" ", 3);
                        clientController.handlePrivateChat(tokensMsg);
                    }
                });
            }
        } catch (Exception e) {
            //display(e.getMessage(), CommonSettings.MESSAGE_TYPE_ADMIN);
            /*quitConnection*/
            closeConnection(QUIT_TYPE_DEFAULT);
            e.printStackTrace();
        }
    }

    public void sendMessageToServer(String message) throws IOException {
        outputStream.write((message + "\r\n").getBytes());
    }

    public void closeConnection(int quitType) {
        if (socket != null) {
            try {
                if (quitType == QUIT_TYPE_DEFAULT)
                    sendMessageToServer("QUIT " + userName + " " + userRoom);
                else if (quitType == QUIT_TYPE_KICK)
                    sendMessageToServer("KICK " + userName + " " + userRoom);
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUserName() {
        return userName;
    }
}


