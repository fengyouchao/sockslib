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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.common.NotImplementException;
import fucksocks.common.ProtocolErrorException;
import fucksocks.common.SocksException;
import fucksocks.common.methods.SocksMethod;
import fucksocks.server.filters.FilterChain;
import fucksocks.server.filters.SocksListener;
import fucksocks.server.io.Pipe;
import fucksocks.server.io.SocketPipe;
import fucksocks.server.msg.CommandMessage;
import fucksocks.server.msg.CommandResponseMessage;
import fucksocks.server.msg.MethodSelectionMessage;
import fucksocks.server.msg.MethodSeleteionResponseMessage;
import fucksocks.server.msg.ServerReply;

/**
 * The class <code>Socks5Handler</code> represents a handler that can handle SOCKS5 protocol.
 *
 * @author Youchao Feng
 * @date Apr 16, 2015 11:03:49 AM
 * @version 1.0
 *
 */
public class Socks5Handler implements SocksHandler {

  /**
   * Logger
   */
  private static final Logger logger = LoggerFactory.getLogger(Socks5Handler.class);

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

  private FilterChain filterChain;

  private int bufferSize;

  private int idleTime = 2000;

  private List<SocksListener> socksListeners;

  @Override
  public void handle(Session session) throws SocksException, IOException {

    sessionCreated(session);

    MethodSelectionMessage msg = new MethodSelectionMessage();
    session.read(msg);

    if (msg.getVersion() != VERSION) {
      throw new ProtocolErrorException("Protocol! error");
    }
    SocksMethod selectedMethod = methodSelector.select(msg);

    logger.debug("[{}]SOKCS5 Server seleted:{}", session,selectedMethod.getMethodName());
    // send select method.
    session.write(new MethodSeleteionResponseMessage(VERSION, selectedMethod));

    // do method.
    selectedMethod.doMethod(session);


    CommandMessage commandMessage = new CommandMessage();


    try {
      session.read(commandMessage); // Read command request.

      logger.info("Session[{}] send Rquest:{}  {}:{}", session.getId(),
          commandMessage.getCommand(), commandMessage.getInetAddress(), commandMessage.getPort());

    } catch (SocksException e) {
      session.write(new CommandResponseMessage(e.getServerReply()));
      logger.debug(e.getMessage());
      e.printStackTrace();
      return;
    }


    commandReceived(session, commandMessage);


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

    // set default bind address.
    byte[] defaultAddress = {0, 0, 0, 0};
    bindAddress = InetAddress.getByAddress(defaultAddress);
    // DO connect
    try {
      socket = new Socket(commandMessage.getInetAddress(), commandMessage.getPort());
      bindAddress = socket.getLocalAddress();
      bindPort = socket.getLocalPort();
      reply = ServerReply.SUCCESSED;

    } catch (IOException e) {
      if (e.getMessage().equals("Connection refused")) {
        reply = ServerReply.CONNECTION_REFUSED;
      } else if (e.getMessage().equals("Operation timed out")) {
        reply = ServerReply.TTL_EXPIRED;
      } else if (e.getMessage().equals("Network is unreachable")) {
        reply = ServerReply.NETWORK_UNREACHABLE;
      }
      logger.debug("connect exception:", e);
    }

    session.write(new CommandResponseMessage(VERSION, reply, bindAddress, bindPort));

    if (reply != ServerReply.SUCCESSED) { // 如果返回失败信息，则退出该方法。
      session.close();
      return;
    }

    Pipe pipe = new SocketPipe(session.getSocket(), socket);
    pipe.setBufferSize(bufferSize);
    pipe.start(); // This method will create tow thread to run tow internal pipes.

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

  }

  @Override
  public void doBind(Session session, CommandMessage commandMessage) throws SocksException,
      IOException {
    throw new NotImplementException("Not implement BIND command");
  }

  @Override
  public void doUDPAssociate(Session session, CommandMessage commandMessage) throws SocksException,
      IOException {
    UDPRelayServer udpRelayServer =
        new UDPRelayServer(((InetSocketAddress) session.getRemoteAddress()).getAddress(),
            commandMessage.getPort());
    InetSocketAddress socketAddress = (InetSocketAddress) udpRelayServer.start();
    logger.info("Create UDP relay server at[{}] for {}", socketAddress,
        commandMessage.getSocketAddress());
    session.write(new CommandResponseMessage(VERSION, ServerReply.SUCCESSED, InetAddress
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

  protected void sessionCreated(Session session) {
    if (socksListeners == null) {
      return;
    }
    for (int i = 0; i < socksListeners.size(); i++) {
      socksListeners.get(i).onSessionCreated(session);
    }
  }

  protected void commandReceived(Session session, CommandMessage message) {
    if (socksListeners == null) {
      return;
    }
    for (int i = 0; i < socksListeners.size(); i++) {
      socksListeners.get(i).onCommandReceived(session, message);;
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
      logger.error("Session[{}]:{}", session.getId(), e.getMessage());
    } finally {
      /*
       * At last, close the session.
       */
      session.close();
      logger.info("Session[{}] closed", session.getId());
    }
  }

  @Override
  public FilterChain getFilterChain() {
    return filterChain;
  }

  @Override
  public void setFilterChain(FilterChain filterChain) {
    this.filterChain = filterChain;
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
  public List<SocksListener> getSocksListeners() {
    return socksListeners;
  }

  @Override
  public void setSocksListeners(List<SocksListener> socksListeners) {
    this.socksListeners = socksListeners;
  }

  @Override
  public int getIdleTime() {
    return idleTime;
  }

  @Override
  public void setIdleTime(int idleTime) {
    this.idleTime = idleTime;
  }

}
