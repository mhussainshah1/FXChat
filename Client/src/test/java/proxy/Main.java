package proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        String serverName = "192.168.1.247";
        int serverPort = 1436;
        String proxyHost = "159.65.242.254";
        int proxyPort = 7497;

        System.setProperty("socksProxyHost", proxyHost);
        System.setProperty("socksProxyPort", String.valueOf(proxyPort));
        System.setProperty("java.net.useSystemProxies", "true");

        SocketAddress proxyAddr = new InetSocketAddress(proxyHost, proxyPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);
        try (Socket socket = new Socket(proxy)) {
            socket.connect(new InetSocketAddress(serverName, serverPort));
//            socket.setSoTimeout(0);
            System.out.println("Connected through proxy server = " + proxyHost + " at port = " + proxyPort);
        }
    }
}