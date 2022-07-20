package com.client;

public interface UserStatusListener {
    public void online(String userName);
    public void offLine(String userName);
}
