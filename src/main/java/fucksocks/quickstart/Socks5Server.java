package fucksocks.quickstart;

import fucksocks.common.methods.NoAuthenticationRequiredMethod;
import fucksocks.common.methods.UsernamePasswordMethod;
import fucksocks.server.SocksProxyServer;
import fucksocks.server.SocksServerBuilder;
import fucksocks.server.manager.MemoryBasedUserManager;
import fucksocks.server.manager.User;
import fucksocks.server.manager.UserManager;
import fucksocks.utils.ArgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * The class <code>Socks5Server</code> can create a simple Socks5 server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 24, 2015 11:25 AM
 */
public class Socks5Server {

  private static final Logger logger = LoggerFactory.getLogger(Socks5Server.class);
  private SocksProxyServer server;

  /**
   * Run a SOCKS5 server. Same as @{@link #start(String[])}.
   *
   * @param args Some arguments.
   * @throws IOException
   */
  public static void main(@Nullable String[] args) throws IOException {
    new Socks5Server().start(args);
  }

  /**
   * Start a SOCKS5 server with some options.
   *
   * @param args Arguments.Support "--h --help --port --auth"
   * @throws IOException
   */
  public void start(@Nullable String[] args) throws IOException {
    int port = 1080;
    String authValue = null;
    if (args != null) {
      for (String arg : args) {
        if (arg.equals("-h") || arg.equals("--help")) {
          showHelp();
          System.exit(0);
        } else if (arg.startsWith("--port=")) {
          port = ArgUtil.intValueOf(arg);
        } else if (arg.startsWith("--auth=")) {
          authValue = ArgUtil.valueOf(arg);
        } else {
          logger.error("Unknown argument[{}]", arg);
          return;
        }
      }
    }
    if (authValue == null) {
      server =
          SocksServerBuilder.newSocks5ServerBuilder().setSocksMethods(new
              NoAuthenticationRequiredMethod()).setBindPort(port).build();
    } else {
      UserManager userManager = new MemoryBasedUserManager();
      String[] users = authValue.split(":");
      String username = users[0];
      String password = users[1];
      userManager.create(new User(username, password));
      server =
          SocksServerBuilder.newSocks5ServerBuilder().setSocksMethods(new UsernamePasswordMethod
              ()).setUserManager(userManager).setBindPort(port).build();
    }
    final SocksProxyServer finalServer = server;
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        finalServer.shutdown();
        logger.info("SOCKS5 sever shutdown");
      }
    }));
    server.start();
  }

  /**
   * Print help information.
   */
  public void showHelp() {
    System.out.println("Usage: [Options]");
    System.out.println("\t--port=<val>\tServer bind port");
    System.out.println("\t--auth=<val1:val2>\tAuthenticate client by username/password");
  }

  /**
   * Shutdown SOCKS5 server.
   */
  public void shutdown() {
    if (server != null) {
      server.shutdown();
    }
  }

}
