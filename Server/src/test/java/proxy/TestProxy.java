package proxy;

import java.io.IOException;
import java.net.*;
import java.util.Properties;

public class TestProxy {
    public static void main(String[] args) throws IOException {
        String serverName = "localhost";
        int serverPort = 1436;
        String proxyHost = "38.154.227.167";
        int proxyPort = 5868;
        final String authUser = "rnjeukzu";
        final String authPassword = "zuiauiidcyz5";


        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", String.valueOf(proxyPort));
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");


        String proxyHostProp = System.getProperty("http.proxyHost");
        int proxyPortProp = Integer.parseInt(System.getProperty("http.proxyPort"));
        String proxyUser = System.getProperty("http.proxyUser");
        String proxyPassword = System.getProperty("http.proxyPassword");


        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v)); // Java 8


        //For Proxy Authentication
        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
            }
        });
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", ""); // By default, basic auth is disabled, by this basic auth is enabled

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostProp, proxyPortProp));
        try (Socket socketProxy = new Socket(proxy);) {
            InetSocketAddress address = InetSocketAddress.createUnresolved(proxyHost, proxyPort); // create a socket without resolving the target host to IP
            socketProxy.connect(address);
            System.out.println("Connected through proxy server = " + proxyHost + " at port = " + proxyPort);
        }
    }
}