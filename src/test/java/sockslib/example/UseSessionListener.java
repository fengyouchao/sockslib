package sockslib.example;

import sockslib.server.SocksProxyServer;
import sockslib.server.SocksServerBuilder;
import sockslib.server.listener.LoggingListener;

import java.io.IOException;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Nov 23, 2015 4:47 PM
 */
public class UseSessionListener {

  public static void main(String[] args) throws IOException {
    SocksProxyServer server = SocksServerBuilder.buildAnonymousSocks5Server();
    server.getSessionManager().onSessionCreate("createLogging", new LoggingListener());
    server.getSessionManager().onSessionClose("CloseSession",
        session -> System.out.println("Close Session:" + session.getId()))
        .onSessionCreate("CreateSession",
            session1 -> System.out.println("Create session" + session1.getId()));
    server.start();
  }
}
