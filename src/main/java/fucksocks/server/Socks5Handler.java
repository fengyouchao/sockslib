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

import fucksocks.client.SocksProxy;
import fucksocks.client.SocksSocket;
import fucksocks.common.AddressType;
import fucksocks.common.NotImplementException;
import fucksocks.common.ProtocolErrorException;
import fucksocks.common.SocksException;
import fucksocks.common.methods.SocksMethod;
import fucksocks.server.filters.SocksCommandFilter;
import fucksocks.server.io.Pipe;
import fucksocks.server.io.SocketPipe;
import fucksocks.server.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * The class <code>Socks5Handler</code> represents a handler that can handle SOCKS5 protocol.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Apr 16, 2015 11:03:49 AM
 */
public class Socks5Handler implements SocksHandler {

  /**
   * Logger that subclasses also can use.
   */
  protected static final Logger logger = LoggerFactory.getLogger(Socks5Handler.class);

  /**
   * Protocol version.
   */
  private static final int VERSION = 0x5;

  /**
   * Session.
   */
  private Session session;

  /**
   * Method selector.
   */
  private MethodSelector methodSelector;

  private int bufferSize;

  private int idleTime = 2000;

  private SocksProxy proxy;

  private List<SocksCommandFilter> socksCommandFilter;

  @Override
  public void handle(Session session) throws Exception {

    MethodSelectionMessage msg = new MethodSelectionMessage();
    session.read(msg);

    if (msg.getVersion() != VERSION) {
      throw new ProtocolErrorException();
    }
    SocksMethod selectedMethod = methodSelector.select(msg);

    logger.debug("SESSION[{}] Response client:{}", session.getId(), selectedMethod.getMethodName());
    // send select method.
    session.write(new MethodSelectionResponseMessage(VERSION, selectedMethod));

    // do method.
    selectedMethod.doMethod(session);

    CommandMessage commandMessage = new CommandMessage();
    session.read(commandMessage); // Read command request.

    logger.info("SESSION[{}] request:{}  {}:{}", session.getId(), commandMessage.getCommand(),
        commandMessage.getAddressType() != AddressType.DOMAIN_NAME ?
            commandMessage.getInetAddress() :
            commandMessage.getHost(), commandMessage.getPort());

    // If there is a SOCKS exception in command message, It will send a right response to client.
    if (commandMessage.hasSocksException()) {
      ServerReply serverReply = commandMessage.getSocksException().getServerReply();
      session.write(new CommandResponseMessage(serverReply));
      logger.info("SESSION[{}] will close, because {}", session.getId(), serverReply);
      return;
    }

    doSocksCommandFilter(session, commandMessage);
    /**************************** DO COMMAND ******************************************/

    switch (commandMessage.getCommand()) {

      case BIND:
        doBind(session, commandMessage);
        break;
      case CONNECT:
        doConnect(session, commandMessage);
        break;
      case UDP_ASSOCIATE:
        doUDPAssociate(session, commandMessage);
        break;
      default:
        throw new NotImplementException("Not support command");

    }



  }

  @Override
  public void doConnect(Session session, CommandMessage commandMessage) throws SocksException,
      IOException {

    ServerReply reply = null;
    Socket socket = null;
    InetAddress bindAddress = null;
    int bindPort = 0;
    InetAddress remoteServerAddress = commandMessage.getInetAddress();
    int remoteServerPort = commandMessage.getPort();

    // set default bind address.
    byte[] defaultAddress = {0, 0, 0, 0};
    bindAddress = InetAddress.getByAddress(defaultAddress);
    // DO connect
    try {
      // Connect directly.
      if (proxy == null) {
        socket = new Socket(remoteServerAddress, remoteServerPort);
      } else {
        socket = new SocksSocket(proxy, remoteServerAddress, remoteServerPort);
      }
      bindAddress = socket.getLocalAddress();
      bindPort = socket.getLocalPort();
      reply = ServerReply.SUCCEEDED;

    } catch (IOException e) {
      if (e.getMessage().equals("Connection refused")) {
        reply = ServerReply.CONNECTION_REFUSED;
      } else if (e.getMessage().equals("Operation timed out")) {
        reply = ServerReply.TTL_EXPIRED;
      } else if (e.getMessage().equals("Network is unreachable")) {
        reply = ServerReply.NETWORK_UNREACHABLE;
      } else if (e.getMessage().equals("Connection timed out")) {
        reply = ServerReply.TTL_EXPIRED;
      } else {
        reply = ServerReply.GENERAL_SOCKS_SERVER_FAILURE;
      }
      logger.info("SESSION[{}] connect {} [{}] exception:{}", session.getId(), new
          InetSocketAddress(remoteServerAddress, remoteServerPort), reply, e.getMessage());
    }

    session.write(new CommandResponseMessage(VERSION, reply, bindAddress, bindPort));

    if (reply != ServerReply.SUCCEEDED) { // 如果返回失败信息，则退出该方法。
      session.close();
      return;
    }

    Pipe pipe = new SocketPipe(session.getSocket(), socket);
    pipe.setName("SESSION[" + session.getId() + "]");
    pipe.setBufferSize(bufferSize);
    pipe.start(); // This method will build tow thread to run tow internal pipes.

    // wait for pipe exit.
    while (pipe.isRunning()) {
      try {
        Thread.sleep(idleTime);
      } catch (InterruptedException e) {
        pipe.stop();
        session.close();
        logger.info("SESSION[{}] closed", session.getId());
      }
    }

  }

  @Override
  public void doBind(Session session, CommandMessage commandMessage) throws SocksException,
      IOException {

    ServerSocket serverSocket = new ServerSocket(commandMessage.getPort());
    int bindPort = serverSocket.getLocalPort();
    Socket socket = null;
    logger.info("Create TCP server bind at {} for session[{}]", serverSocket
        .getLocalSocketAddress(), session.getId());
    session.write(new CommandResponseMessage(VERSION, ServerReply.SUCCEEDED, serverSocket
        .getInetAddress(), bindPort));

    socket = serverSocket.accept();
    session.write(new CommandResponseMessage(VERSION, ServerReply.SUCCEEDED, socket
        .getLocalAddress(), socket.getLocalPort()));

    Pipe pipe = new SocketPipe(session.getSocket(), socket);
    pipe.setBufferSize(bufferSize);
    pipe.start();

    // wait for pipe exit.
    while (pipe.isRunning()) {
      try {
        Thread.sleep(idleTime);
      } catch (InterruptedException e) {
        pipe.stop();
        session.close();
        logger.info("Session[{}] closed", session.getId());
      }
    }
    serverSocket.close();
    // throw new NotImplementException("Not implement BIND command");
  }

  @Override
  public void doUDPAssociate(Session session, CommandMessage commandMessage) throws
      SocksException, IOException {
    UDPRelayServer udpRelayServer =
        new UDPRelayServer(((InetSocketAddress) session.getClientAddress()).getAddress(),
            commandMessage.getPort());
    InetSocketAddress socketAddress = (InetSocketAddress) udpRelayServer.start();
    logger.info("Create UDP relay server at[{}] for {}", socketAddress, commandMessage
        .getSocketAddress());
    session.write(new CommandResponseMessage(VERSION, ServerReply.SUCCEEDED, InetAddress
        .getLocalHost(), socketAddress.getPort()));
    while (udpRelayServer.isRunning()) {
      try {
        Thread.sleep(idleTime);
      } catch (InterruptedException e) {
        session.close();
        logger.info("Session[{}] closed", session.getId());
      }
      if (session.isClose()) {
        udpRelayServer.stop();
        logger.debug("UDP relay server for session[{}] is closed", session.getId());
      }

    }

  }

  @Override
  public void setSession(Session session) {
    this.session = session;
  }


  @Override
  public void run() {
    try {
      handle(session);
    } catch (Exception e) {
      logger.error("SESSION[{}]: {}", session.getId(), e.getMessage());
      logger.error(e.getMessage(), e);
    } finally {
      /*
       * At last, close the session.
       */
      session.close();
      logger.info("SESSION[{}] closed", session.getId());
    }
  }

  @Override
  public MethodSelector getMethodSelector() {
    return methodSelector;
  }

  @Override
  public void setMethodSelector(MethodSelector methodSelector) {
    this.methodSelector = methodSelector;
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
  public List<SocksCommandFilter> getSocksCommandFilters() {
    return socksCommandFilter;
  }

  @Override
  public void setSocksCommandFilters(List<SocksCommandFilter> socksCommandFilter) {
    this.socksCommandFilter = socksCommandFilter;
  }

  @Override
  public int getIdleTime() {
    return idleTime;
  }

  @Override
  public void setIdleTime(int idleTime) {
    this.idleTime = idleTime;
  }

  public SocksProxy getProxy() {
    return proxy;
  }

  @Override
  public void setProxy(SocksProxy proxy) {
    this.proxy = proxy;
  }

  protected void doSocksCommandFilter(Session session, CommandMessage message) throws Exception {

    if (socksCommandFilter != null && socksCommandFilter.size() > 0) {

      boolean isInterrupted = false;
      SocksCommandFilter socksFilter = null;

      for (int i = 0; i < socksCommandFilter.size(); i++) {
        socksFilter = socksCommandFilter.get(i);
        boolean bl = socksFilter.doFilter(session, message);
        if (!bl) {
          isInterrupted = true;
          break;
        }
      }

      if (isInterrupted) {
        throw new InterruptedException("Interrupted by " + socksFilter.getClass());
      }

    }
  }

}
