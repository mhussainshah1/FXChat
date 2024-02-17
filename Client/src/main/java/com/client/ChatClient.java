package com.client;

import com.controller.ClientController;
import com.controller.tab.TabPaneManagerController;
import com.entity.User;
import javafx.application.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.function.BiConsumer;

import static com.common.CommonSettings.*;

public class ChatClient {
    private final ClientController clientController;
    private final TabPaneManagerController tabPaneManagerController;
    private final User user;
    private final BiConsumer<Serializable, Integer> onReceiveCallback;
    private String serverData;
    private Socket socket;
    private BufferedReader bufferedReader;
    private OutputStream outputStream;

    public ChatClient(ClientController clientController, TabPaneManagerController tabPaneManagerController, User user, BiConsumer<Serializable, Integer> onReceiveCallback) {
        this.clientController = clientController;
        this.tabPaneManagerController = tabPaneManagerController;
        this.user = user;
        this.onReceiveCallback = onReceiveCallback;
    }

    public void startConnection(boolean isProxy, String code) throws IOException {
        if (isProxy) {
            //Proxy
/*
            SocksSocketImplFactory factory = new SocksSocketImplFactory(proxyHost, proxyPort);
            SocksSocket.setSocketImplFactory(factory);
            socket = new SocksSocket(serverName, serverPort);
*/
            System.setProperty("http.proxyHost", user.getProxyHost());
            System.setProperty("http.proxyPort", String.valueOf(user.getProxyPort()));
            System.setProperty("java.net.useSystemProxies", "true");

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(user.getProxyHost(), user.getProxyPort()));
            socket = new Socket(proxy);
            socket.connect(new InetSocketAddress(user.getServerName(), user.getServerPort()));
            socket.setSoTimeout(0);
            System.out.println("Connected to proxy server = " + user.getProxyHost() + " at port = " + user.getProxyPort());

        } else {
            //Not Proxy
            socket = new Socket(user.getServerName(), user.getServerPort());
        }
        socket.setTcpNoDelay(true);
        outputStream = socket.getOutputStream();
        sendMessageToServer(code + " " + user.getUserName() + " " + user.getPassword() + " " + user.getRoomName());
        //sendMessageToServer("HELO " + userName + " " + userRoom);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Send HELO To Server
        startMessageReader();
    }

    private void startMessageReader() {
        Thread t = new Thread(this::readMessageLoop);
        t.start();
    }

    private void readMessageLoop() {
        try {
            while ((serverData = bufferedReader.readLine()) != null) {
                System.out.println(serverData);

                String[] tokens = serverData.split(" ");
                String command = tokens[0];


                Platform.runLater(() -> {
                    // LIST RFC
                    if (command.equalsIgnoreCase("LIST")) {
                        tabPaneManagerController.getUsersTabController().handleList(tokens);
                    }

                    // ROOM RFC
                    else if (command.equalsIgnoreCase("ROOM")) {
                        tabPaneManagerController.getRoomsTabController().handleRoom(tokens);
                    }

                    // ADD user RFC
                    else if (command.equalsIgnoreCase("ADD")) {
                        tabPaneManagerController.getUsersTabController().handleLogin(tokens);
                    }

                    //EXCP <<Username Already Exists>>
                    else if (command.equalsIgnoreCase("EXCP")) {
                        String[] tokensMsg = serverData.split(" ", 2);
                        clientController.handleException(tokensMsg);
                    }

                    // REMOVE User RFC
                    else if (command.equalsIgnoreCase("REMO")) {
                        tabPaneManagerController.getUsersTabController().handleLoggOff(tokens);
                    }

                    // MESS RFC
                    else if (command.equalsIgnoreCase("MESS")) {
                        String[] tokensMsg = serverData.split(" ", 3);
                        tabPaneManagerController.getUsersTabController().handleMessage(tokensMsg);
                    }

                    // KICK RFC
                    else if (command.equalsIgnoreCase("KICK")) {
                        clientController.handleKickUser();
                    }

                    // INKI RFC (Information about kicked off User
                    else if (command.equalsIgnoreCase("INKI")) {
                        tabPaneManagerController.getUsersTabController().handleKickUserInfo(tokens);
                    }

                    // Change Room RFC
                    else if (command.equalsIgnoreCase("CHRO")) {
                        user.setRoomName(tokens[1]);
                        tabPaneManagerController.getRoomsTabController().handleChangeRoom(user.getRoomName());
                    }

                    // Join Room RFC
                    else if (command.equalsIgnoreCase("JORO")) {
                        tabPaneManagerController.getUsersTabController().handleJoinRoom(tokens);
                    }

                    // Leave Room RFC
                    else if (command.equalsIgnoreCase("LERO")) {
                        tabPaneManagerController.getUsersTabController().handleLeaveRoom(tokens);
                    }

                    // Room Count RFC
                    else if (command.equalsIgnoreCase("ROCO")) {
                        tabPaneManagerController.getRoomsTabController().handleRoomCount(tokens);
                    }

                    // Private Message RFC
                    else if (command.equalsIgnoreCase("PRIV")) {
                        String[] tokensMsg = serverData.split(" ", 3);
                        tabPaneManagerController.getUsersTabController().handlePrivateChat(tokensMsg);
                    }
                });
            }
        } catch (IOException e) {
            //display(e.getMessage(), CommonSettings.MESSAGE_TYPE_ADMIN);
            /*quitConnection*/
            closeConnection(QUIT_TYPE_DEFAULT);
            onReceiveCallback.accept(e, MESSAGE_TYPE_ADMIN);
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
                    sendMessageToServer("QUIT " + user.getUserName() + " " + user.getRoomName());
                else if (quitType == QUIT_TYPE_KICK)
                    sendMessageToServer("KICK " + user.getUserName() + " " + user.getRoomName());
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


