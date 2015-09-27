package fucksocks.test;

import fucksocks.common.methods.UsernamePasswordMethod;
import fucksocks.server.SocksProxyServer;
import fucksocks.server.SocksServerBuilder;
import fucksocks.server.manager.MongoDBBasedUserManager;
import fucksocks.server.manager.UserManager;

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
