package proxy;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProxySelector extends ProxySelector {
    // Keep a reference on the previous default
    ProxySelector defsel = null;
    /*
     * A list of proxies, indexed by their address.
     */
    Map<SocketAddress, InnerProxy> proxies = new HashMap<>();

    MyProxySelector(ProxySelector def) {
        // Save the previous default
        defsel = def;

        // Populate the HashMap (List of proxies)
        InnerProxy i = new InnerProxy(new InetSocketAddress("34.125.221.146", 80));
        proxies.put(i.address(), i);
        i = new InnerProxy(new InetSocketAddress("208.109.191.161", 1080));
        proxies.put(i.address(), i);
        i = new InnerProxy(new InetSocketAddress("167.99.180.31", 29277));
        proxies.put(i.address(), i);
    }

    public static void main(String[] args) throws URISyntaxException {
        MyProxySelector ps = new MyProxySelector(ProxySelector.getDefault());
        ProxySelector.setDefault(ps);
        // rest of the application

        List<Proxy> l = ProxySelector.getDefault().select(new URI("http://www.yahoo.com/"));
        System.out.println(l);
    }

    /*
     * This is the method that the handlers will call.
     * Returns a List of proxy.
     */
    public java.util.List<Proxy> select(URI uri) {
        // Let's stick to the specs.
        if (uri == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }

        /*
         * If it's a http (or https) URL, then we use our own
         * list.
         */
        String protocol = uri.getScheme();
        if ("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) {
            ArrayList<Proxy> l = new ArrayList<>();
            for (InnerProxy p : proxies.values()) {
                l.add(p.toProxy());
            }
            return l;
        }

        /*
         * Not HTTP or HTTPS (could be SOCKS or FTP)
         * defer to the default selector.
         */
        if (defsel != null) {
            return defsel.select(uri);
        } else {
            ArrayList<Proxy> l = new ArrayList<Proxy>();
            l.add(Proxy.NO_PROXY);
            return l;
        }
    }

    /*
     * Method called by the handlers when it failed to connect
     * to one of the proxies returned by select().
     */
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        // Let's stick to the specs again.
        if (uri == null || sa == null || ioe == null) {
            throw new IllegalArgumentException("Arguments can't be null.");
        }

        /*
         * Let's lookup for the proxy
         */
        InnerProxy p = proxies.get(sa);
        if (p != null) {
            /*
             * It's one of ours, if it failed more than 3 times
             * let's remove it from the list.
             */
            if (p.failed() >= 3)
                proxies.remove(sa);
        } else {
            /*
             * Not one of ours, let's delegate to the default.
             */
            if (defsel != null)
                defsel.connectFailed(uri, sa, ioe);
        }
    }

    /*
     * Inner class representing a Proxy and a few extra data
     */
    class InnerProxy {
        Proxy proxy;
        SocketAddress addr;
        // How many times did we fail to reach this proxy?
        int failedCount = 0;

        InnerProxy(InetSocketAddress a) {
            addr = a;
            proxy = new Proxy(Proxy.Type.HTTP, a);
        }

        SocketAddress address() {
            return addr;
        }

        Proxy toProxy() {
            return proxy;
        }

        int failed() {
            return ++failedCount;
        }
    }
}
