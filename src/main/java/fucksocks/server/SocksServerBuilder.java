package fucksocks.server;

import fucksocks.client.SocksProxy;
import fucksocks.common.methods.NoAuthencationRequiredMethod;
import fucksocks.common.methods.SocksMethod;
import fucksocks.common.methods.UsernamePasswordMethod;
import fucksocks.server.manager.FileBasedUserManager;
import fucksocks.server.manager.RamBasedUserManager;
import fucksocks.server.manager.UserManager;
import fucksocks.server.filters.SessionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fengyouchao on 8/27/15.
 *
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

    private SocksServerBuilder(Class<? extends SocksHandler> socksHandlerClass) {
        if (socksHandlerClass == null) {
            throw new IllegalArgumentException("socksHandlerClass can't be null");
        }
        this.socksHandlerClass = socksHandlerClass;
    }

    ;

    public static SocksProxyServer buildAnonymousSocks5Server() {
        return buildAnonymousSocks5Server(DEFAULT_PORT);
    }

    public static SocksProxyServer buildAnonymousSocks5Server(int bindPort) {
        return newSocks5ServerBuilder().setSupportedSocksMethod(new NoAuthencationRequiredMethod())
            .setBindPort(bindPort).build();
    }

    public static SocksServerBuilder newBuilder(Class<? extends SocksHandler> socksHandlerClass) {
        if (socksHandlerClass == null) {
            throw new IllegalArgumentException("socksHandlerClass can't be null");
        }
        return new SocksServerBuilder(socksHandlerClass);
    }

    public static SocksServerBuilder newSocks5ServerBuilder() {
        return new SocksServerBuilder(Socks5Handler.class);
    }

    public SocksServerBuilder addSupportedSocksMethods(SocksMethod... methods) {
        if (supportedSocksMethods == null) {
            supportedSocksMethods = new HashSet<>();
        }
        Collections.addAll(supportedSocksMethods, methods);
        return this;
    }

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
        SocksMethod[] methods = new SocksMethod[supportedSocksMethods.size()];
        int i = 0;
        for (SocksMethod method : supportedSocksMethods) {
            if (method instanceof UsernamePasswordMethod) {
                if (userManager == null) {
                    userManager = new RamBasedUserManager();
                    userManager.addUser("fucksocks", "123456");
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
