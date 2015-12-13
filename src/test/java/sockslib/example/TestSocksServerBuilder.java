package sockslib.example;

import sockslib.common.methods.UsernamePasswordMethod;
import sockslib.server.SocksProxyServer;
import sockslib.server.SocksServerBuilder;
import sockslib.server.manager.MongoDBBasedUserManager;
import sockslib.server.manager.UserManager;

import java.io.IOException;

/**
 * <code>TestSocksServerBuilder</code> is example code to show you how to create a SOCKS5 server
 * using {@link SocksServerBuilder}<br>
 * Created by fengyouchao on 8/29/15.
 *
 * @author fengyouchao
 * @version 1.0
 */
public class TestSocksServerBuilder {

  public static void main(String[] args) {
    UserManager userManager = MongoDBBasedUserManager.newDefaultUserManager();
    SocksProxyServer server =
        SocksServerBuilder.newSocks5ServerBuilder().setUserManager(userManager).setSocksMethods
            (new UsernamePasswordMethod()).build();
    try {
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
