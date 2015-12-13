package sockslib.example;

import sockslib.common.IPRange;
import sockslib.common.methods.NoAuthenticationRequiredMethod;
import sockslib.server.SocksProxyServer;
import sockslib.server.SocksServerBuilder;
import sockslib.server.listener.CloseSessionException;

import java.io.IOException;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Nov 24, 2015 2:15 PM
 */
public class AddressLimit {

  public static void main(String[] args) throws IOException {
    SocksProxyServer server = SocksServerBuilder.buildAnonymousSocks5Server();
    SocksServerBuilder.newSocks5ServerBuilder().addSocksMethods(new NoAuthenticationRequiredMethod());
    server.getSessionManager().onSessionCreate("filterByIP", session -> {
      IPRange allowedAddress = IPRange.parse("127.0.0.1-127.0.0.1");
      if (!allowedAddress.contains(session.getClientAddress())) {
        throw new CloseSessionException("IP not allowed");
      }
    });
    server.start();
  }
}
