package com.server;

import java.io.Serializable;
import java.util.function.Consumer;

public abstract class ServerNetWorkConnection {
    private final Consumer<Serializable> onReceiveCallback;
    public ServerNetWorkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
    }
    protected abstract String getIP();
    protected abstract int getPort();
}