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

import fucksocks.common.methods.SocksMethod;
import fucksocks.server.filters.FilterChain;
import fucksocks.server.filters.SocksListener;

/**
 * The class <code>GenericSocksProxyServer</code> is a implementation of {@link SocksProxyServer}.
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

  private int timeout = 10000;

  private MethodSelector methodSelector = new SocksMethodSelector();

  private FilterChain filterChain;

  private int bufferSize = 1024 * 1024 * 5;

  private List<SocksListener> socksListeners;


  public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass) {
    this(socketHandlerClass, Executors.newFixedThreadPool(THREAD_NUMBER));
  }

  public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass,
      ExecutorService executorService) {
    this.socksHandlerClass = socketHandlerClass;
    this.executorService = executorService;
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
        logger.info("Create {}", session);

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
        e.printStackTrace();
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
      e.printStackTrace();
    }

  }

  @Override
  public void start() throws IOException {
    start(DEFAULT_SOCKS_PORT);
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
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void initializeSocksHandler(SocksHandler socksHandler) {
    socksHandler.setMethodSelector(methodSelector);
    socksHandler.setFilterChain(filterChain);
    socksHandler.setBufferSize(bufferSize);
    socksHandler.setSocksListeners(socksListeners);
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
  public void setSupportedMethod(SocksMethod... methods) {
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
  public void addSocksListenner(SocksListener socksListener) {
    if (socksListener == null) {
      socksListeners = new ArrayList<>();
    }
    socksListeners.add(socksListener);
  }

  @Override
  public void removeSocksListenner(SocksListener socksListener) {
    if (socksListener == null) {
      return;
    }
    socksListeners.remove(socksListener);
  }

}
