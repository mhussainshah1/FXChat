package proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jcgonzalez.com
 */
public class ProxyMultiThread {
    public static void main(String[] args) {
        String host = "38.154.227.167"; //args[0];
        int remoteport = 5868; //Integer.parseInt(args[1]);
        int localport = 1436; //Integer.parseInt(args[2]);
        try (ServerSocket server = new ServerSocket(localport);){
           /* if (args.length != 3)
                throw new IllegalArgumentException("insufficient arguments");*/
            // and the local port that we listen for connections on
            // Print a start-up message
            System.out.println("Starting proxy for " + host + ":" + remoteport + " on port " + localport);
            while (true) {
                new ThreadProxy(server.accept(), host, remoteport);
            }
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: java ProxyMultiThread <host> <remoteport> <localport>");
        }
    }
}

/**
 * Handles a socket connection to the proxy server from the client and uses 2
 * threads to proxy between server and client
 *
 * @author jcgonzalez.com
 */
class ThreadProxy extends Thread {
    private final String SERVER_URL;
    private final int SERVER_PORT;
    private final Socket sClient;

    ThreadProxy(Socket sClient, String ServerUrl, int ServerPort) {
        this.SERVER_URL = ServerUrl;
        this.SERVER_PORT = ServerPort;
        this.sClient = sClient;
        this.start();
    }

    @Override
    public void run() {
        try {
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = sClient.getInputStream();
            final OutputStream outToClient = sClient.getOutputStream();
            Socket client = null, server = null;
            // connects a socket to the server
            try {
                server = new Socket(SERVER_URL, SERVER_PORT);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = server.getInputStream();
            final OutputStream outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread(() -> {
                int bytes_read;
                try {
                    while ((bytes_read = inFromClient.read(request)) != -1) {
                        outToServer.write(request, 0, bytes_read);
                        outToServer.flush();
                        //TODO CREATE YOUR LOGIC HERE
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outToServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                    //TODO CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            sClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}