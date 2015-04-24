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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.common.Socks5DatagramPacketHandler;

/**
 * The class <code>UDPRelayServer</code> represents a UDP relay server.
 * 
 * @author Youchao Feng
 * @date Apr 22, 2015 12:54:50 AM
 * @version 1.0
 *
 */
public class UDPRelayServer implements Runnable {

  /**
   * Logger.
   */
  protected static final Logger logger = LoggerFactory.getLogger(UDPRelayServer.class);

  /**
   * SOCKS5 datagram packet handle.
   */
  private Socks5DatagramPacketHandler datagramPacketHandler = new Socks5DatagramPacketHandler();

  /**
   * UDP server.
   */
  private DatagramSocket server;

  /**
   * Buffer size.
   */
  private int bufferSize = 1024 * 1024 * 5;

  /**
   * Running thread.
   */
  private Thread thread;

  /**
   * A status flag.
   */
  private boolean running = false;

  /**
   * Client address.
   */
  private InetAddress clientAddresss;

  /**
   * Client port.
   */
  private int clientPort;


  public UDPRelayServer() {}

  public UDPRelayServer(InetAddress clientInetAddress, int clientPort) {
    this(new InetSocketAddress(clientInetAddress, clientPort));
  }

  public UDPRelayServer(SocketAddress clientSocketAddresss) {
    if (clientSocketAddresss instanceof InetSocketAddress) {
      clientAddresss = ((InetSocketAddress) clientSocketAddresss).getAddress();
      clientPort = ((InetSocketAddress) clientSocketAddresss).getPort();
    } else {
      throw new IllegalArgumentException("Only support java.net.InetSocketAddress");
    }
  }

  /**
   * Starts a UDP relay server.
   * 
   * @return Server bind socket address.
   * @throws SocketException If a SOCKS protocol error occurred.
   */
  public SocketAddress start() throws SocketException {
    running = true;
    server = new DatagramSocket();
    SocketAddress socketAddress = server.getLocalSocketAddress();
    thread = new Thread(this);
    thread.start();
    return socketAddress;
  }

  public void stop() {
    if (running) {
      running = false;
      thread.interrupt();
      if (!server.isClosed()) {
        server.close();
      }
    }
  }

  @Override
  public void run() {
    try {
      byte[] recvBuf = new byte[bufferSize];
      DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
      while (running) {
        server.receive(packet);
        if (isFromClient(packet)) {
          datagramPacketHandler.decapsulate(packet);
          server.send(packet);
        } else {
          packet =
              datagramPacketHandler.encapsulate(packet, new InetSocketAddress(clientAddresss,
                  clientPort));
          server.send(packet);
        }

      }
    } catch (IOException e) {
      if (e.getMessage().equals("Socket closed") && !running) {
        logger.debug("UDP relay server stoped");
      } else {
        e.printStackTrace();
      }
    }
  }

  public DatagramSocket getServer() {
    return server;
  }

  public void setServer(DatagramSocket server) {
    this.server = server;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public Socks5DatagramPacketHandler getDatagramPacketHandler() {
    return datagramPacketHandler;
  }

  public void setDatagramPacketHandler(Socks5DatagramPacketHandler datagramPacketHandler) {
    this.datagramPacketHandler = datagramPacketHandler;
  }

  public InetAddress getClientAddresss() {
    return clientAddresss;
  }

  public void setClientAddresss(InetAddress clientAddresss) {
    this.clientAddresss = clientAddresss;
  }

  public int getClientPort() {
    return clientPort;
  }

  public void setClientPort(int clientPort) {
    this.clientPort = clientPort;
  }

  public boolean isRunning() {
    return running;
  }

  protected boolean isFromClient(DatagramPacket packet) {

    if (packet.getPort() == clientPort && clientAddresss.equals(packet.getAddress())) {
      return true;
    }
    // client is in local.
    else if (packet.getPort() == clientPort && clientAddresss.getHostAddress().startsWith("127.")) {
      return true;
    }
    return false;
  }

}
