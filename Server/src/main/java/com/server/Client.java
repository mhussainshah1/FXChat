package com.server;//Chat Server Client Object

import java.net.Socket;

public class Client {
    Socket socket;
    String userName;
    String roomName;

    public Client(Socket socket, String userName, String roomName) {
        this.socket = socket;
        this.userName = userName;
        this.roomName = roomName;
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