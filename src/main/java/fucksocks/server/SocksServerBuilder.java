package fucksocks.server;

import fucksocks.client.SocksProxy;
import fucksocks.common.methods.NoAuthenticationRequiredMethod;
import fucksocks.common.methods.SocksMethod;
import fucksocks.common.methods.UsernamePasswordMethod;
import fucksocks.server.filters.SessionFilter;
import fucksocks.server.manager.MemoryBasedUserManager;
import fucksocks.server.manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
  private Set<SocksMethod> supportedSocksMethods;
  private UserManager userManager;
  private SocksProxy proxy;
  private int timeout;
  private int bindPort = DEFAULT_PORT;
  private ExecutorService executorService;

  /**
   * Creates a <code>SocksServerBuilder</code> with a <code>Class<? extends {@link SocksHandler}</code>
   * instance.
   *
   * @param socksHandlerClass <code>java.lang.Class<? extends {@link SocksHandler}</code> instance.
   */
  private SocksServerBuilder(Class<? extends SocksHandler> socksHandlerClass) {
    if (socksHandlerClass == null) {
      throw new IllegalArgumentException("socksHandlerClass can't be null");
    }
    this.socksHandlerClass = socksHandlerClass;
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
    return newSocks5ServerBuilder().setSupportedSocksMethod(new NoAuthenticationRequiredMethod()).setBindPort(bindPort).build();
  }

  /**
   * Creates a <code>SocksServerBuilder</code> instance with specified Class instance of {@link SocksHandler}.
   *
   * @param socksHandlerClass Class instance of {@link SocksHandler}.
   * @return Instance of {@link SocksServerBuilder}.
   */
  public static SocksServerBuilder newBuilder(Class<? extends SocksHandler> socksHandlerClass) {
    if (socksHandlerClass == null) {
      throw new IllegalArgumentException("socksHandlerClass can't be null");
    }
    return new SocksServerBuilder(socksHandlerClass);
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
  public SocksServerBuilder addSupportedSocksMethods(SocksMethod... methods) {
    if (supportedSocksMethods == null) {
      supportedSocksMethods = new HashSet<>();
    }
    Collections.addAll(supportedSocksMethods, methods);
    return this;
  }

  /**
   * Set SOCKS methods that SOCKS server will support.
   *
   * @param methods Instance of {@link SocksMethod}.
   * @return Instance of {@link SocksServerBuilder}.
   */
  public SocksServerBuilder setSupportedSocksMethod(SocksMethod... methods) {
    if (supportedSocksMethods == null) {
      supportedSocksMethods = new HashSet<>();
    }
    Collections.addAll(supportedSocksMethods, methods);
    return this;
  }

  public SocksServerBuilder setSupportedSocksMethod(Set<SocksMethod> methods) {
    if (methods == null) {
      throw new IllegalArgumentException("methods can't be null");
    }
    supportedSocksMethods = methods;
    return this;
  }

  public SocksServerBuilder setUserManager(UserManager userManager) {
    if (userManager == null) {
      throw new IllegalArgumentException("userManager can't be null");
    }
    this.userManager = userManager;
    return this;
  }

  public SocksServerBuilder setSessionFilter(SessionFilter sessionFilter) {
    return null;
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
    this.executorService = executorService;
    return this;
  }

  public SocksProxyServer build() {
    SocksProxyServer proxyServer = new GenericSocksProxyServer(socksHandlerClass);
    proxyServer.setTimeout(timeout);
    proxyServer.setBindPort(bindPort);
    if (supportedSocksMethods == null) {
      supportedSocksMethods = new HashSet<>();
      supportedSocksMethods.add(new NoAuthenticationRequiredMethod());
    }
    SocksMethod[] methods = new SocksMethod[supportedSocksMethods.size()];
    int i = 0;
    for (SocksMethod method : supportedSocksMethods) {
      if (method instanceof UsernamePasswordMethod) {
        if (userManager == null) {
          userManager = new MemoryBasedUserManager();
          userManager.addUser("fucksocks", "fucksocks");
        }
        ((UsernamePasswordMethod) method).setAuthenticator(new UsernamePasswordAuthenticator(userManager));
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
