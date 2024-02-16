package com.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;
    private String password;
    private String roomName;
    private int serverPort;
    private String serverName;
    private int maximumGuestNumber;
    private List<String> roomList;
    private boolean proxyState;
    private int proxyPort;
    private String proxyHost;
    private List<Client> clients;

    public User() {
        userName = "Anonymous";
        password = "Anonymous";
        roomName = "General";
        serverPort = 1436;
        serverName = "localhost";
        maximumGuestNumber = 50;
        roomList = new ArrayList<>();
        proxyState = false;
        proxyHost = "";
        proxyPort = 0;
        clients = new ArrayList<>();
    }

    public User(String userName, String password, String roomName, String serverName, int serverPort, int maximumGuestNumber, List<String> roomList, boolean proxyState, String proxyHost, int proxyPort) {
        this();
        this.userName = userName;
        this.password = password;
        this.roomName = roomName;
        this.serverPort = serverPort;
        this.serverName = serverName;
        this.maximumGuestNumber = maximumGuestNumber;
        this.roomList = roomList;
        this.proxyState = proxyState;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getMaximumGuestNumber() {
        return maximumGuestNumber;
    }

    public void setMaximumGuestNumber(int maximumGuestNumber) {
        this.maximumGuestNumber = maximumGuestNumber;
    }

    public List<String> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<String> roomList) {
        this.roomList = roomList;
    }

    public boolean isProxyState() {
        return proxyState;
    }

    public void setProxyState(boolean proxyState) {
        this.proxyState = proxyState;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}