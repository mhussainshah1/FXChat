package com.client;

import java.io.Serializable;
import java.util.function.Consumer;

public class Client extends ClientNetworkConnection implements Serializable {
    private String userName;
    private String serverName;//IP
    private int serverPort;
    private String proxyHost;
    private int proxyPort;
    private boolean proxy;
    public Client(){
    }
    public Client(String serverName, int serverPort, String userName, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isProxy() {
        return proxy;
    }

    public void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    @Override
    public String toString() {
        return "ClientObject{" +
                "userName='" + userName + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverPort=" + serverPort +
                ", proxyHost='" + proxyHost + '\'' +
                ", proxyPort=" + proxyPort +
                ", proxy=" + proxy +
                '}';
    }
}
