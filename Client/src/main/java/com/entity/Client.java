package com.entity;

import org.springframework.stereotype.Component;

@Component
public class Client {
    private final String clientName;
    private boolean ignored;

    public Client() {
        this.clientName = "";
        ignored = false;
    }

    public Client(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }


    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
