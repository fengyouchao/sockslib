package socklib.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

public class Ports {

    private Ports() {
    }

    private static final String LOCALHOST_IP = "127.0.0.1";

    public static SocketAddress localSocketAddress(int port) {
        return new InetSocketAddress(LOCALHOST_IP, port);
    }

    public static int unused() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

}
