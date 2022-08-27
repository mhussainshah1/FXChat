package com.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.common.CommonSettings.COMPANY_NAME;
import static com.common.CommonSettings.PRODUCT_NAME;

public class Data implements AutoCloseable {
    private final InputStream inputStream;
    private final FileOutputStream fileOutputStream;
    private boolean proxyState;
    private int proxyPort;
    private String proxyHost;
    private String userName;
    private int serverPort;
    private String serverName;
    private int maximumGuestNumber;
    private String roomList;
    private Properties properties;

    public Data(String fileName) throws IOException {
        this.inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        this.fileOutputStream = new FileOutputStream(fileName);
        this.properties = new Properties();

        properties = getProperties();
//        properties.forEach((x, y) -> System.out.println(x + " = " + y));

        if (properties.getProperty("ServerName") != null)
            serverName = properties.getProperty("ServerName");
        else serverName = "localhost";

        if (properties.getProperty("PortNumber") != null) {
            serverPort = Integer.parseInt(properties.getProperty("PortNumber"));
        } else serverPort = 1436;

        if (properties.getProperty("UserName") != null)
            userName = properties.getProperty("UserName");
        else userName = "Anonymous";

        if (properties.getProperty("MaximumGuest") != null)
            maximumGuestNumber = Integer.parseInt(properties.getProperty("MaximumGuest"));
        else maximumGuestNumber = 50;

        if (properties.getProperty("roomList") != null)
            roomList = properties.getProperty("roomList");
        else roomList = "General Teen Music Party";

        if (properties.getProperty("ProxyHost") != null)
            proxyHost = properties.getProperty("ProxyHost");
        else proxyHost = "";

        if (properties.getProperty("ProxyState") != null)
            proxyState = Boolean.parseBoolean(properties.getProperty("ProxyState"));
        else proxyState = false;

        if (properties.getProperty("ProxyPort") != null)
            proxyPort = Integer.parseInt(properties.getProperty("ProxyPort"));
        else proxyPort = 0;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        properties.setProperty("ServerPort", String.valueOf(serverPort));
        this.serverPort = serverPort;
    }

    public int getMaximumGuestNumber() {
        return maximumGuestNumber;
    }

    public void setMaximumGuestNumber(int maximumGuestNumber) {
        properties.setProperty("MaximumGuest", String.valueOf(maximumGuestNumber));
        this.maximumGuestNumber = maximumGuestNumber;
    }

    public String getRoomList() {
        return roomList;
    }

    public void setRoomList(String roomList) {
        if (properties.getProperty("roomList") != null)
            roomList = properties.getProperty("roomList") + ";" + roomList;
        this.roomList = roomList;
    }

    //Loading Properties File
    private Properties getProperties() throws IOException {
        //Getting the Property Value From Property File
        if (inputStream != null)
            properties.load(inputStream);
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
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
        properties.setProperty("ProxyPort", String.valueOf(proxyPort));
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        properties.setProperty("ProxyHost", proxyHost);
        this.proxyHost = proxyHost;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        properties.setProperty("ServerName", serverName);
        this.serverName = serverName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        properties.setProperty("UserName", userName);
        this.userName = userName;
    }

    private void store() throws IOException {
        if (fileOutputStream != null)
            properties.store(fileOutputStream, PRODUCT_NAME + " " + COMPANY_NAME);
    }

    @Override
    public void close() throws IOException {
        store();
        if (inputStream != null)
            inputStream.close();

        if (fileOutputStream != null)
            fileOutputStream.close();
    }
}
