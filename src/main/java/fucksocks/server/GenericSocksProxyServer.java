/*
 * Copyright 2015-2025 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package fucksocks.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.client.SocksProxy;
import fucksocks.common.methods.SocksMethod;
import fucksocks.server.filters.SessionFilter;
import fucksocks.server.filters.SessionFilterChain;
import fucksocks.server.filters.SocksCommandFilter;

/**
 * The class <code>GenericSocksProxyServer</code> is a implementation of {@link SocksProxyServer}.<br>
 * You can create a SOKCS5 server easily by following codes:<br>
 * 
 * <pre>
 * ProxyServer proxyServer = new GenericSocksProxyServer(Socks5Handler.class);
 * proxyServer.start(); // Create a SOCKS5 server bind at 1080.
 * </pre>
 * 
 * If you want change the port, you can using following codes:
 * 
 * <pre>
 * proxyServer.start(9999);
 * </pre>
 * 
 * 
 * @author Youchao Feng
 * @date Apr 19, 2015 1:10:17 PM
 * @version 1.0
 *
 */
public class GenericSocksProxyServer implements SocksProxyServer, Runnable {

  protected static final Logger logger = LoggerFactory.getLogger(GenericSocksProxyServer.class);

  /**
   * Number of threads in thread pool.
   */
  private static final int THREAD_NUMBER = 100;

  /**
   * Thread pool used to process each connection.
   */
  private ExecutorService executorService;

  /**
   * The next session's ID.
   */
  private long nextSessionId = 0;

  /**
   * Server socket.
   */
  private ServerSocket serverSocket;

  /**
   * SOCKS socket handler class.
   */
  private Class<? extends SocksHandler> socksHandlerClass;

  /**
   * Sessions that server managed.
   */
  private Map<Long, Session> sessions;

  /**
   * A flag.
   */
  private boolean stop = false;

  /**
   * Thread that start the server.
   */
  private Thread thread;

  /**
   * Session filter chain.
   */
  private SessionFilterChain sessionFilterChain = new SessionFilterChain();

  /**
   * Timeout for a session.
   */
  private int timeout = 10000;

  /**
   * Method selector.
   */
  private MethodSelector methodSelector = new SocksMethodSelector();

  /**
   * Buffer size.
   */
  private int bufferSize = 1024 * 1024 * 5;

  /**
   * SOCKS listeners.
   */
  private List<SocksCommandFilter> socksListeners;

  private int bindPort = DEFAULT_SOCKS_PORT;

  private SocksProxy proxy;


  /**
   * Constructs a {@link GenericSocksProxyServer} by a {@link SocksHandler} class. The bind port is
   * 1080.
   * 
   * @param socketHandlerClass {@link SocksHandler} class.
   */
  public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass) {
    this(socketHandlerClass, DEFAULT_SOCKS_PORT, Executors.newFixedThreadPool(THREAD_NUMBER));
  }

  /**
   * Constructs a {@link GenericSocksProxyServer} by a {@link SocksHandler} class and a port.
   * 
   * @param socketHandlerClass {@link SocksHandler} class.
   * @param port The port that SOCKS server will listen.
   */
  public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass, int port) {
    this(socketHandlerClass, port, Executors.newFixedThreadPool(THREAD_NUMBER));
  }

  /**
   * Constructs a {@link GenericSocksProxyServer} by a {@link SocksHandler} class and a
   * ExecutorService.
   * 
   * @param socketHandlerClass {@link SocksHandler} class.
   * @param executorService Thread pool.
   */
  public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass,
      ExecutorService executorService) {
    this(socketHandlerClass, DEFAULT_SOCKS_PORT, executorService);
  }

  /**
   * Constructs a {@link GenericSocksProxyServer} by a {@link SocksHandler} class , a port and a
   * ExecutorService.
   * 
   * @param socketHandlerClass {@link SocksHandler} class.
   * @param port The port that SOCKS server will listen.
   * @param executorService Thread pool.
   */
  public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass, int port,
      ExecutorService executorService) {
    this.socksHandlerClass = socketHandlerClass;
    this.executorService = executorService;
    this.bindPort = port;
    sessions = new HashMap<>();
  }

  @Override
  public void run() {
    while (!stop) {
      try {
        Socket socket = serverSocket.accept();
        socket.setSoTimeout(timeout);
        Session session = new SocksSession(getNextSessionId(), socket, sessions);
        sessions.put(session.getId(), session);
        logger.info("Create SESSION[{}] for {}", session.getId(), session.getRemoteAddress());

        try {
          sessionFilterChain.doFilterWork(session);
        } catch (InterruptedException e) {
          session.close();
          logger.info(e.getMessage());
          continue;
        }

        SocksHandler socksHandler = createSocksHandler();

        /* initialize socks handler */
        socksHandler.setSession(session);
        initializeSocksHandler(socksHandler);

        executorService.execute(socksHandler);

      } catch (IOException e) {
        // Catches the exception that cause by shutdown method.
        if (e.getMessage().equals("Socket closed") && stop) {
          logger.debug("Server shutdown");
          return;
        }
        logger.debug(e.getMessage(), e);
      }
    }
  }

  public void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public void shutdown() {
    stop = true;
    if (thread != null) {
      thread.interrupt();
    }
    try {
      closeAllSession();
      if (serverSocket != null && serverSocket.isBound()) {
        serverSocket.close();
      }
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }

  }

  @Override
  public void start() throws IOException {
    start(bindPort);
  }

  @Override
  public void start(int bindPort) throws IOException {
    serverSocket = new ServerSocket(bindPort);
    thread = new Thread(this);
    thread.start();

    logger.info("Start proxy server at port:{}", bindPort);
  }


  @Override
  public SocksHandler createSocksHandler() {
    try {
      return socksHandlerClass.newInstance();
    } catch (InstantiationException e) {
      logger.error(e.getMessage(), e);
    } catch (IllegalAccessException e) {
      logger.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public void initializeSocksHandler(SocksHandler socksHandler) {
    socksHandler.setMethodSelector(methodSelector);
    socksHandler.setBufferSize(bufferSize);
    socksHandler.setSocksCommandFilters(socksListeners);
    socksHandler.setProxy(proxy);
  }

  /**
   * Closes all sessions.
   */
  protected void closeAllSession() {
    for (long key : sessions.keySet()) {
      sessions.get(key).close();
    }

  }

  public ExecutorService getExecutorService() {
    return executorService;
  }

  private synchronized long getNextSessionId() {
    nextSessionId++;
    return nextSessionId;
  }

  @Override
  public Map<Long, Session> getManagedSessions() {
    return sessions;
  }

  @Override
  public void setSupportMethods(SocksMethod... methods) {
    methodSelector.setSupportMethod(methods);
  }

  @Override
  public int getTimeout() {
    return timeout;
  }

  @Override
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  @Override
  public int getBufferSize() {
    return bufferSize;
  }

  @Override
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  @Override
  public void addSocksCommandFilter(SocksCommandFilter socksListener) {
    if (socksListeners == null) {
      socksListeners = new ArrayList<>();
    }
    socksListeners.add(socksListener);
  }

  @Override
  public void removeSocksCommandFilter(SocksCommandFilter socksListener) {
    if (socksListener == null) {
      return;
    }
    socksListeners.remove(socksListener);
  }

  @Override
  public void addSessionFilter(SessionFilter sessionFilter) {
    sessionFilterChain.addFilter(sessionFilter);
  }

  @Override
  public void removeSessionFilter(SessionFilter sessionFilter) {
    sessionFilterChain.remoteFilter(sessionFilter);
  }

  @Override
  public SocksProxy getProxy() {
    return proxy;
  }

  @Override
  public void setProxy(SocksProxy proxy) {
    this.proxy = proxy;
  }

  @Override
  public int getBindPort() {
    return bindPort;
  }

  @Override
  public void setBindPort(int bindPort) {
    this.bindPort = bindPort;
  }

  public SessionFilterChain getSessionFilterChain() {
    return sessionFilterChain;
  }

  public void setSessionFilterChain(SessionFilterChain sessionFilterChain) {
    this.sessionFilterChain = sessionFilterChain;
  }


}
