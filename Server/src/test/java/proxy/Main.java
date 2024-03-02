package proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        String proxyHost = "128.199.5.121";//"147.45.40.100";
        int proxyPort = 32339;//33441;

        System.setProperty("socksProxyHost", proxyHost);
        System.setProperty("socksProxyPort", String.valueOf(proxyPort));
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

        SocketAddress proxyAddr = new InetSocketAddress(proxyHost, proxyPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);
        try (Socket socket = new Socket(proxy)) {
            InetSocketAddress address = InetSocketAddress.createUnresolved(proxyHost, proxyPort); // create a socket without resolving the target host to IP
            socket.connect(address);
            socket.setSoTimeout(0);
            System.out.println("Connected through proxy server = " + proxyHost + " at port = " + proxyPort);
        }
    }
}