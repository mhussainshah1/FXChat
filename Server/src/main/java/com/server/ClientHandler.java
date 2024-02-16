package com.server;

//Chat Server Client Communication Object

import com.common.DBUtils;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.common.CommonSettings.*;

public class ClientHandler extends Thread implements CommonSettings {
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    private final BiConsumer<Serializable, Integer> onReceiveCallback;
    private final ChatServer server;
    private final ArrayList<String> messages;
    //Global Variable Declarations
    private Socket socket;
    private BufferedReader bufferedIn;
    private String RFC;
    private OutputStream outputStream;
    private ClientHandler clientHandler;
    private String userName;
    private String roomName;

    //Initialize the Socket to the Client
    ClientHandler(ChatServer server, Socket socket, BiConsumer<Serializable, Integer> onReceiveCallback) {
        this.server = server;
        this.socket = socket;
        this.onReceiveCallback = onReceiveCallback;
        messages = new ArrayList<>();
    }

    /********Implement the Thread Interface *********/
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            removeUserWhenException(socket);
            quitConnection();
            //e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        bufferedIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while ((RFC = bufferedIn.readLine()) != null) {
            System.out.println(RFC);
            String[] tokens = RFC.split(" ");
            if (tokens != null && tokens.length > 0) {
                String command = tokens[0];
//                Arrays.asList(tokens).forEach(System.out::println);

                //RFC Checking
                //LOGN username password room
                if (command.equalsIgnoreCase("LOGN")) {
                    loginUser(socket, tokens);
                }
                //SGUP ali password General
                else if (command.equalsIgnoreCase("SGUP")) {
                    signupUser(socket, tokens);
                }
                //HELO ali General
                /*else if (command.equalsIgnoreCase("HELO")) {
                    addUser(socket, tokens);
                }*/
                //QUIT ali Party
                else if (command.equalsIgnoreCase("QUIT")) {
                    removeUser(tokens, REMOVE_USER);
                    quitConnection();
                }
                //KICK amir Party
                else if (command.equalsIgnoreCase("KICK")) {
                    removeUser(tokens, KICK_USER);
                    quitConnection();
                }
                //CHRO amir Party
                else if (command.equalsIgnoreCase("CHRO")) {
                    changeRoom(socket, tokens);
                }
                //MESS General ali <hi how are you>
                else if (command.equalsIgnoreCase("MESS")) {
                    String[] tokensMsg = RFC.split(" ", 4);
                    sendGeneralMessage(socket, tokensMsg);
                }
                //PRIV ali amir <This is private message>
                else if (command.equalsIgnoreCase("PRIV")) {
                    String[] tokensMsg = RFC.split(" ", 4);
                    sendPrivateMessage(tokensMsg);
                }
                //ROCO General
                else if (command.equalsIgnoreCase("ROCO")) {
                    getUserCount(socket, tokens);
                }
                //CALL amir~ali
                else if (command.equalsIgnoreCase("CALL")) {
                    requestForVoiceChat(socket, tokens);
                }
                //ACCE amir~ali
                else if (command.equalsIgnoreCase("ACCE")) {
                    sendUserIP(socket, tokens);
                }
                //CANC amir~ali
                else if (command.equalsIgnoreCase("CANC")) {
                    rejectCall(tokens);
                }
                //QVCT amir~ali
                else if (command.equalsIgnoreCase("QVCT")) {
                    quitVoiceChat(tokens);
                }
                //REIP amir~ali
                else if (command.equalsIgnoreCase("REIP")) {
                    getRemoteUserAddress(socket, tokens);
                }
                //AEIP amir~ali
                else if (command.equalsIgnoreCase("AEIP")) {
                    sendRemoteUserAddress(socket, tokens);
                }
                //QUVC amir~ali
                else if (command.equalsIgnoreCase("QUVC")) {
                    quitVideoChat(tokens);
                }
            }
        }
    }

    private void quitConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
    }

    //Function To Send a Message to Client
    private void sendMessageToClient(Socket socket, String message) {
        try {
            outputStream = socket.getOutputStream();
            outputStream.write((message + "\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Function To Get the Object Of Given Username
    private ClientHandler getClientHandlerByUserName(String userName) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUserName().equalsIgnoreCase(userName)) {
                return clientHandler;
            }
        }
        return null;
    }

    //Function To Check whether the Username is Already Exists
    private boolean isUserExists(String userName) {
        return getClientHandlerByUserName(userName) != null;
    }

    protected void loginUser(Socket socket, String[] tokens) {
        String tokenUserName = tokens[1];
        String tokenPassword = tokens[2];
        String tokenRoomName = tokens[3];

        try {
            DBUtils.logInUser(tokenUserName, tokenPassword);
            addUser(socket, new String[]{tokenUserName, tokenRoomName});
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient(socket, "EXCP " + e.getMessage());
        }
    }

    protected void signupUser(Socket socket, String[] tokens) {
        String tokenUserName = tokens[1];
        String tokenPassword = tokens[2];
        String tokenRoomName = tokens[3];

        try {
            DBUtils.signUpUser(tokenUserName, tokenPassword, tokenRoomName);
            addUser(socket, new String[]{tokenUserName, tokenRoomName});
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient(socket, "EXCP " + e.getMessage());
        }
    }

    //Function To Add a New Client in to the Server List
    protected void addUser(Socket socket, String[] tokens) {

        String tokenUserName = tokens[0];
        String tokenRoomName = tokens[1];

        //If Username Exists return
        if (isUserExists(tokenUserName)) {
            sendMessageToClient(socket, "EXCP User name already exists... try again with another name!");
            return;
        }

        //Send a Room List
        sendMessageToClient(socket, "ROOM " + server.getRoomList());

        //Send the New User Detail into All Other Users
        String addRFC = "ADD " + tokenUserName;
        StringBuilder builder = new StringBuilder("LIST ");

        for (ClientHandler client : clientHandlers) {
            //Check the Room Name
            if (client.getRoomName().equals(tokenRoomName)) {
                sendMessageToClient(client.getSocket(), addRFC);
                builder.append(client.getUserName())
                        .append(" ");
            }
        }

        //Add a user in to array list
        setUserName(tokenUserName);
        setRoomName(tokenRoomName);
        //client = new ClientHandler(socket, userName, ROOM_NAME);
        clientHandlers.add(this);

        //Sending the Complete User List to the New User
        builder.append(tokenUserName)
                .append(" ");
        sendMessageToClient(socket, builder.toString());

        onReceiveCallback.accept(tokenUserName + " joins chat...", MESSAGE_TYPE_JOIN);
    }

    //Function to Remove User From Server
    public void removeUser(String[] tokens, int removeType) {
        String tokenUserName = tokens[1];
        String tokenRoomName = tokens[2];

        ClientHandler removeClient = getClientHandlerByUserName(tokenUserName);
        if (removeClient != null) {
            clientHandlers.remove(removeClient);
            //clients.trimToSize();

            String removeRFC = null;
            if (removeType == REMOVE_USER)
                removeRFC = "REMO " + tokenUserName;
            if (removeType == KICK_USER)
                removeRFC = "INKI " + tokenUserName;

            //Send a REMO RFC to all others Users
            for (ClientHandler client : clientHandlers) {
                if (client.getRoomName().equals(tokenRoomName))
                    sendMessageToClient(client.getSocket(), removeRFC);
            }
            onReceiveCallback.accept(tokenUserName + " has been logged out from chat!", MESSAGE_TYPE_LEAVE);
        }
    }

    //Remove User When Exception Occurs
    protected void removeUserWhenException(Socket socket) {
        for (ClientHandler removeClient : clientHandlers) {
            if (removeClient.getSocket().equals(socket)) {
                String removeUserName = removeClient.getUserName();
                String removeRoomName = removeClient.getRoomName();
                clientHandlers.remove(removeClient);
                //clients.trimToSize();
                String removeRFC = "REMO " + removeUserName;

                //Send a REMO RFC to all others Users
                for (ClientHandler client : clientHandlers) {
                    if (client.getRoomName().equals(removeRoomName))
                        sendMessageToClient(client.getSocket(), removeRFC);
                }
                return;
            }
        }
//        onReceiveCallback.accept(userName + " has been logged out from chat!", MESSAGE_TYPE_LEAVE);
    }

    //Function To Change the Room
    public void changeRoom(Socket socket, String[] tokens) {
        String tokenUserName = tokens[1];
        String newRoomName = tokens[2];

        ClientHandler tempClient = getClientHandlerByUserName(tokenUserName);
        if (tempClient != null) {
            //Update the Old Room to New Room and send the RFC
            String oldRoomName = tempClient.getRoomName();
            tempClient.setRoomName(newRoomName);

            sendMessageToClient(socket, "CHRO " + newRoomName);

            //Send all the Users list of that particular room to that client socket
            StringBuilder builder = new StringBuilder("LIST ");
            for (ClientHandler client : clientHandlers) {
                //Check the Room Name
                if (client.getRoomName().equals(newRoomName)) {
                    builder.append(client.getUserName())
                            .append(" ");
                }
            }
            sendMessageToClient(socket, builder.toString());

            //Inform to Old Room and New Room Users
            String oldRoomRFC = "LERO " + tokenUserName + " " + newRoomName;
            String newRoomRFC = "JORO " + tokenUserName;
            for (ClientHandler client : clientHandlers) {
                if (client.getRoomName().equals(oldRoomName))
                    sendMessageToClient(client.getSocket(), oldRoomRFC);
                if ((client.getRoomName().equals(newRoomName)) && (!(client.getUserName().equals(tokenUserName))))
                    sendMessageToClient(client.getSocket(), newRoomRFC);
            }

            onReceiveCallback.accept(tokenUserName + " has left " + oldRoomName + " Room and joined into " + newRoomName + " Room", MESSAGE_TYPE_ADMIN);
        }
    }

    //Function to Send General Message
    protected void sendGeneralMessage(Socket socket, String[] tokens) {
        String tokenRoomName = tokens[1];
        String tokenUserName = tokens[2];
        String message = tokens[3];

        boolean floodFlag = false;
        messages.add(tokenUserName);
        if (messages.size() > MAX_MESSAGE) {
            messages.remove(0);
            messages.trimToSize();

            //Chk Whether the User is flooding the message
            String firstMessage = messages.get(0);
            for (int i = 1; i < messages.size(); i++) {
                if (messages.get(i).equals(firstMessage)) {
                    floodFlag = true;
                } else {
                    floodFlag = false;
                    break;
                }
            }
        }

        //Sending a General Message to All the Users
        String messageRFC = "MESS " + tokenUserName + " " + message;
        for (ClientHandler client : clientHandlers) {
            if ((client.getRoomName().equals(tokenRoomName)) && (!(client.getUserName().equals(tokenUserName)))) {
                sendMessageToClient(client.getSocket(), messageRFC);
            }
        }

        //Kick Off the User If he/she flooding the message
        if (floodFlag) {
            sendMessageToClient(socket, "KICK ");
            messages.clear();
        }

        onReceiveCallback.accept(tokenUserName + ": " + message, MESSAGE_TYPE_DEFAULT);
    }

    //Function To Send Private Message
    protected void sendPrivateMessage(String[] tokens) {
        String toUserName = tokens[1];
        String tokenUserName = tokens[2];
        String message = tokens[3];

        clientHandler = getClientHandlerByUserName(toUserName);
        if (clientHandler != null) {
            sendMessageToClient(clientHandler.getSocket(), "PRIV " + tokenUserName + " " + message);
        }
    }

    //Function To Request User For Voice Chat
    protected void requestForVoiceChat(Socket socket, String[] tokens) {
        String fromUserName = tokens[1];
        String toUserName = tokens[2];

        clientHandler = getClientHandlerByUserName(toUserName);
        if (clientHandler != null) {
            sendMessageToClient(clientHandler.getSocket(), "REQU " + getClientHandlerByUserName(fromUserName).getSocket().getInetAddress().getHostAddress() + " " + fromUserName);
        }
    }

    //Function To Quit Voice Chat
    protected void quitVoiceChat(String[] tokens) {
        String fromUserName = tokens[1];
        String toUserName = tokens[2];

        System.out.println(fromUserName + "-->" + toUserName);
        clientHandler = getClientHandlerByUserName(toUserName);
        if (clientHandler != null) {
            sendMessageToClient(clientHandler.getSocket(), "QVCT " + fromUserName + " " + toUserName);
        }
    }

    //Function To Send User IP For Voice Chat
    protected void sendUserIP(Socket ClientSocket, String[] tokens) {
        String fromUserName = tokens[1];
        String toUserName = tokens[2];

        clientHandler = getClientHandlerByUserName(toUserName);
        if (ClientSocket != null) {
            sendMessageToClient(clientHandler.getSocket(), "ADDR " + getClientHandlerByUserName(fromUserName).getSocket().getInetAddress().getHostAddress() + "~" + fromUserName);
        }
    }

    //Function To Reject The Request For Voice Chat
    protected void rejectCall(String[] tokens) {
        String fromUserName = tokens[1];
        String toUserName = tokens[2];

        clientHandler = getClientHandlerByUserName(toUserName);
        if (clientHandler != null) {
            sendMessageToClient(clientHandler.getSocket(), "REJC " + fromUserName + " " + toUserName);
        }
    }

    //Function to Get Remote User Address
    protected void getRemoteUserAddress(Socket ClientSocket, String[] tokens) {
        String fromUserName = tokens[1];
        String toUserName = tokens[2];

        clientHandler = getClientHandlerByUserName(toUserName);
        if (clientHandler != null) {
            sendMessageToClient(clientHandler.getSocket(), "REIP " + fromUserName + " " + ClientSocket.getInetAddress().getHostAddress());
        }
    }

    //Function to Get Remote User Address
    protected void sendRemoteUserAddress(Socket ClientSocket, String[] tokens) {
        String fromUserName = tokens[1];
        String toUserName = tokens[2];

        clientHandler = getClientHandlerByUserName(fromUserName);
        if (clientHandler != null) {
            sendMessageToClient(clientHandler.getSocket(), "AEIP " + toUserName + " " + ClientSocket.getInetAddress().getHostAddress());
        }
    }

    //Function to Quit Video Chat
    protected void quitVideoChat(String[] tokens) {
        String toUserName = tokens[1];
        clientHandler = getClientHandlerByUserName(toUserName);
        if (clientHandler != null) {
            sendMessageToClient(clientHandler.getSocket(), "QUVC");
        }
    }

    //Function to get the User Count in the Room
    protected void getUserCount(Socket ClientSocket, String[] tokens) {
        String tokenRoomName = tokens[1];
        int userCount = 0;
        for (ClientHandler client : clientHandlers) {
            if (client.getRoomName().equals(tokenRoomName))
                userCount++;
        }

        sendMessageToClient(ClientSocket, "ROCO " + tokenRoomName + " " + userCount);
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            /*if (message.startsWith("Server: ") ||
                    !clientHandler.userName.equals(userName)) {*/
            String messageToSend = "MESS Server " + message;
            clientHandler.sendMessageToClient(clientHandler.getSocket(), messageToSend);
//            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}