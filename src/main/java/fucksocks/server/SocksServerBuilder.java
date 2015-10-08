package fucksocks.server;

import fucksocks.client.SocksProxy;
import fucksocks.common.SSLConfiguration;
import fucksocks.common.methods.NoAuthenticationRequiredMethod;
import fucksocks.common.methods.SocksMethod;
import fucksocks.common.methods.UsernamePasswordMethod;
import fucksocks.server.manager.MemoryBasedUserManager;
import fucksocks.server.manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class <code>SocksServerBuilder</code> is a tool class to build an {@link SocksProxyServer}.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Aug 27, 2015
 * @since JDK1.7
 */
public class SocksServerBuilder {

  private static final Logger logger = LoggerFactory.getLogger(SocksServerBuilder.class);
  private static final int DEFAULT_PORT = 1080;

  private Class<? extends SocksHandler> socksHandlerClass;
  private Set<SocksMethod> socksMethods;
  private UserManager userManager;
  private SocksProxy proxy;
  private int timeout;
  private int bindPort = DEFAULT_PORT;
  private boolean daemon = false;
  private ExecutorService executorService;
  private SessionManager sessionManager = new BasicSessionManager();
  private SSLConfiguration sslConfiguration;

  /**
   * Creates a <code>SocksServerBuilder</code> with a <code>Class<? extends {@link
   * SocksHandler}</code>
   * instance.
   *
   * @param socksHandlerClass <code>java.lang.Class<? extends {@link SocksHandler}</code> instance.
   */
  private SocksServerBuilder(Class<? extends SocksHandler> socksHandlerClass) {
    this.socksHandlerClass =
        checkNotNull(socksHandlerClass, "Argument [socksHandlerClass] may not be null");
    userManager = new MemoryBasedUserManager();
  }

  /**
   * Builds a {@link SocksProxyServer} which support SOCKS5 protocol.
   * This SOCKS5 server will accept all requests from clients with no authentication.
   *
   * @return Instance of {@link SocksProxyServer}.
   */
  public static SocksProxyServer buildAnonymousSocks5Server() {
    return buildAnonymousSocks5Server(DEFAULT_PORT);
  }

  /**
   * Builds a {@link SocksProxyServer} which support SOCKS5 protocol bind at a specified port.
   * This SOCKS5 server will accept all requests from clients with no authentication.
   *
   * @param bindPort The port that server listened.
   * @return Instance of {@link SocksProxyServer}.
   */
  public static SocksProxyServer buildAnonymousSocks5Server(int bindPort) {
    return newSocks5ServerBuilder().setSocksMethods(new NoAuthenticationRequiredMethod())
        .setBindPort(bindPort).build();
  }

  /**
   * Builds a SSL based {@link SocksProxyServer} with no authentication required.
   *
   * @param bindPort The port that server listened.
   * @param configuration SSL configuration
   * @return Instance of {@link SocksProxyServer}
   */
  public static SocksProxyServer buildAnonymousSSLSocks5Server(int bindPort, SSLConfiguration configuration){
    return newSocks5ServerBuilder().setSocksMethods(new NoAuthenticationRequiredMethod())
        .setBindPort(bindPort).useSSL(configuration).build();
  }

  public static SocksProxyServer buildAnonymousSSLSocks5Server(SSLConfiguration configuration){
    return buildAnonymousSSLSocks5Server(DEFAULT_PORT, configuration);
  }

  /**
   * Creates a <code>SocksServerBuilder</code> instance with specified Class instance of {@link
   * SocksHandler}.
   *
   * @param socksHandlerClass Class instance of {@link SocksHandler}.
   * @return Instance of {@link SocksServerBuilder}.
   */
  public static SocksServerBuilder newBuilder(Class<? extends SocksHandler> socksHandlerClass) {
    return new SocksServerBuilder(
        checkNotNull(socksHandlerClass, "Argument [socksHandlerClass] may not be null"));
  }

  /**
   * Calls {@link #newBuilder(Class)}  with an argument <code>Socks5Handler.class</code>;
   *
   * @return Instance of {@link SocksServerBuilder}.
   */
  public static SocksServerBuilder newSocks5ServerBuilder() {
    return new SocksServerBuilder(Socks5Handler.class);
  }

  /**
   * Add {@link SocksMethod}.
   *
   * @param methods Instance of {@link SocksMethod}.
   * @return Instance of {@link SocksServerBuilder}.
   */
  public SocksServerBuilder addSocksMethods(SocksMethod... methods) {
    if (socksMethods == null) {
      socksMethods = new HashSet<>();
    }
    Collections.addAll(socksMethods, methods);
    return this;
  }

  /**
   * Set SOCKS methods that SOCKS server will support.
   *
   * @param methods Instance of {@link SocksMethod}.
   * @return Instance of {@link SocksServerBuilder}.
   */
  public SocksServerBuilder setSocksMethods(SocksMethod... methods) {
    if (socksMethods == null) {
      socksMethods = new HashSet<>();
    }
    Collections.addAll(socksMethods, methods);
    return this;
  }

  public SocksServerBuilder setSocksMethods(Set<SocksMethod> methods) {
    socksMethods = checkNotNull(methods, "Argument [methods] may not be null");
    return this;
  }

  public SocksServerBuilder setUserManager(UserManager userManager) {
    this.userManager = checkNotNull(userManager, "Argument [userManager] may not be null");
    return this;
  }

  public SocksServerBuilder setProxy(SocksProxy proxy) {
    this.proxy = proxy;
    return this;
  }

  public SocksServerBuilder setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  public SocksServerBuilder setTimeout(long timeout, TimeUnit timeUnit) {
    this.timeout = (int) timeUnit.toMillis(timeout);
    return this;
  }

  public SocksServerBuilder setBindPort(int bindPort) {
    this.bindPort = bindPort;
    return this;
  }

  public SocksServerBuilder setExecutorService(ExecutorService executorService) {
    this.executorService = checkNotNull(executorService);
    return this;
  }

  public SocksServerBuilder setDaemon(boolean daemon) {
    this.daemon = daemon;
    return this;
  }

  public SocksServerBuilder setSessionManager(SessionManager sessionManager) {
    this.sessionManager = checkNotNull(sessionManager);
    return this;
  }

  public SocksServerBuilder useSSL(SSLConfiguration sslConfiguration) {
    this.sslConfiguration = sslConfiguration;
    return this;
  }

  public SocksProxyServer build() {
    SocksProxyServer proxyServer = null;
    if (sslConfiguration == null) {
      proxyServer = new BasicSocksProxyServer(socksHandlerClass);
    } else {
      proxyServer = new SSLSocksProxyServer(socksHandlerClass, sslConfiguration);
    }
    proxyServer.setTimeout(timeout);
    proxyServer.setBindPort(bindPort);
    proxyServer.setDaemon(daemon);
    proxyServer.setSessionManager(sessionManager);
    if (socksMethods == null) {
      socksMethods = new HashSet<>();
      socksMethods.add(new NoAuthenticationRequiredMethod());
    }
    SocksMethod[] methods = new SocksMethod[socksMethods.size()];
    int i = 0;
    for (SocksMethod method : socksMethods) {
      if (method instanceof UsernamePasswordMethod) {
        if (userManager == null) {
          userManager = new MemoryBasedUserManager();
          userManager.addUser("fucksocks", "fucksocks");
        }
        ((UsernamePasswordMethod) method)
            .setAuthenticator(new UsernamePasswordAuthenticator(userManager));
      }
      methods[i] = method;
      i++;
    }
    if (executorService != null) {
      proxyServer.setExecutorService(executorService);
    }

    proxyServer.setSupportMethods(methods);
    if (proxy != null) {
      proxyServer.setProxy(proxy);
    }
    return proxyServer;
  }
}
