package com.client;

import java.io.Serializable;
import java.util.function.Consumer;

public class Client extends ClientNetworkConnection implements Serializable {
	private final String ip;
	private final int port;
	private final String userName;

	public Client(String ip, int port, String userName, Consumer<Serializable> onReceiveCallback) {
		super(onReceiveCallback);
		this.ip = ip;
		this.port = port;
		this.userName = userName;
	}

	@Override
	protected String getIP() {
		return ip;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	protected int getPort() {
		return port;
	}

}