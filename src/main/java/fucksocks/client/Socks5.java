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

package fucksocks.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.common.AnonymousAuthentication;
import fucksocks.common.Authentication;
import fucksocks.common.SocksCommand;
import fucksocks.common.SocksException;
import fucksocks.common.UsernamePasswordAuthentication;
import fucksocks.common.methods.GssApiMethod;
import fucksocks.common.methods.NoAuthencationRequiredMethod;
import fucksocks.common.methods.SocksMethod;
import fucksocks.common.methods.UsernamePasswordMethod;

/**
 * The class <code>Socks5</code> has implements SOCKS5 protocol.
 * 
 * @author Youchao Feng
 * @date Mar 16, 2015 4:57:32 PM
 * @version 1.0
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1928.txt">SOCKS Protocol Version 5</a>
 */
public class Socks5 implements SocksProxy {

  /**
   * Logger.
   */
  protected static final Logger logger = LoggerFactory.getLogger(Socks5.class);

  /**
   * Authentication.
   */
  private Authentication authentication = new AnonymousAuthentication();

  /**
   * SOCKS5 server's address. IPv4 or IPv6 address.
   */
  private InetAddress inetAddress;
  /**
   * SOCKS5 server's port;
   */
  private int port = SOCKS_DEFAULT_PORT;

  /**
   * The socket that will connect to SOCKS5 server.
   */
  private Socket proxySocket;

  /**
   * SOCKS5 client acceptable methods.
   */
  private List<SocksMethod> acceptableMethods;

  /**
   * Use to send a request to SOCKS server and receive a method that SOCKS server selected .
   */
  private SocksMethodRequestor socksMethodRequestor = new GenericSocksMethodRequestor();

  /**
   * Use to send command to SOCKS5 sever
   */
  private SocksCommandSender socksCmdSender = new GenericSocksCommandSender();

  /**
   * Resolve remote server's domain name in SOCKS server if it's false. It's default false.
   */
  private boolean alwaysResolveAddressLocally = false;

  /**
   * Constructs a Socks5 instance without any parameter.
   * 
   */
  public Socks5() {
    acceptableMethods = new ArrayList<>();
    acceptableMethods.add(new NoAuthencationRequiredMethod());
    acceptableMethods.add(new GssApiMethod());
    acceptableMethods.add(new UsernamePasswordMethod());
  }

  /**
   * Constructs a Socks5 instance with a java.net.SocketAddress instance.
   * 
   * @param socketAddress SOCKS5 server's address.
   */
  public Socks5(SocketAddress socketAddress) {
    this();
    inetAddress = ((InetSocketAddress) socketAddress).getAddress();
    port = ((InetSocketAddress) socketAddress).getPort();
  }

  /**
   * Constructs a Socks5 instance.
   * 
   * @param socketAddress SOCKS5 server's address.
   * @param username Username of the authentication.
   * @param password Password of the authentication.
   */
  public Socks5(SocketAddress socketAddress, String username, String password) {
    this(socketAddress);
    setAuthentication(new UsernamePasswordAuthentication(username, password));
  }

  /**
   * Constructs a Socks5 instance.
   * 
   * @param inetAddress SOCKS5 server's address.
   * @param port SOCKS5 server's port.
   */
  public Socks5(InetAddress inetAddress, int port) {
    this(new InetSocketAddress(inetAddress, port));
  }

  /**
   * Constructs a Socks5 instance.
   * 
   * @param host SOCKS5's server host.
   * @param port SOCKS5's server port.
   * @throws UnknownHostException If the host can't be resolved.
   */
  public Socks5(String host, int port) throws UnknownHostException {
    this(InetAddress.getByName(host), port);
  }

  /**
   * Constructs a Socks instance.
   * 
   * @param host SOCKS5 server's host.
   * @param port SOCKS5 server's port.
   * @param username Username of the authentication.
   * @param password Password of the authentication.
   * @throws UnknownHostException If the host can't be resolved.
   */
  public Socks5(String host, int port, String username, String password)
      throws UnknownHostException {
    this();
    this.inetAddress = InetAddress.getByName(host);
    this.port = port;
    this.authentication = new UsernamePasswordAuthentication(username, password);
  }

  @Override
  public void buildConnection() throws SocksException, IOException {
    if (inetAddress == null) {
      throw new IllegalArgumentException("Please set inetAddress before calling buildConnection.");
    }
    if (proxySocket == null) {
      proxySocket = new Socket(inetAddress, port);
    } else {
      proxySocket.connect(new InetSocketAddress(inetAddress, port));
    }

    SocksMethod method =
        socksMethodRequestor.doRequest(acceptableMethods, proxySocket, SOCKS_VERSION);
    method.doMethod(this);
  }

  @Override
  public CommandReplyMesasge reqeustConnect(String host, int port) throws SocksException,
      IOException {
    if (!alwaysResolveAddressLocally) {
      // resolve address in SOCKS server
      return socksCmdSender.send(proxySocket, SocksCommand.CONNECT, host, port, SOCKS_VERSION);
    } else {
      // resolve address in local.
      InetAddress address = InetAddress.getByName(host);
      return socksCmdSender.send(proxySocket, SocksCommand.CONNECT, address, port, SOCKS_VERSION);
    }
  }

  @Override
  public CommandReplyMesasge reqeustConnect(InetAddress address, int port) throws SocksException,
      IOException {
    return socksCmdSender.send(proxySocket, SocksCommand.CONNECT, address, port, SOCKS_VERSION);
  }

  @Override
  public CommandReplyMesasge reqeustConnect(SocketAddress address) throws SocksException,
      IOException {
    return socksCmdSender.send(proxySocket, SocksCommand.CONNECT, address, SOCKS_VERSION);
  }

  @Override
  public CommandReplyMesasge requestBind(String host, int port) throws SocksException, IOException {
    return socksCmdSender.send(proxySocket, SocksCommand.BIND, host, port, SOCKS_VERSION);
  }

  @Override
  public CommandReplyMesasge requestBind(InetAddress inetAddress, int port) throws SocksException,
      IOException {
    return socksCmdSender.send(proxySocket, SocksCommand.BIND, inetAddress, port, SOCKS_VERSION);
  }

  @Override
  public Socket accept() throws SocksException, IOException {
    CommandReplyMesasge messge = socksCmdSender.checkServerReply(proxySocket.getInputStream());
    logger.debug("accept a connection from:{}",messge.getSocketAddress());
    return this.proxySocket;
  }

  @Override
  public CommandReplyMesasge requestUdpAssociat(String host, int port) throws SocksException,
      IOException {
    return socksCmdSender.send(proxySocket, SocksCommand.UDP_ASSOCIATE, new InetSocketAddress(host,
        port), SOCKS_VERSION);
  }

  @Override
  public CommandReplyMesasge requestUdpAssociat(InetAddress address, int port)
      throws SocksException, IOException {
    return socksCmdSender.send(proxySocket, SocksCommand.UDP_ASSOCIATE, new InetSocketAddress(
        address, port), SOCKS_VERSION);
  }

  @Override
  public int getPort() {
    return port;
  }

  @Override
  public Socks5 setPort(int port) {
    this.port = port;
    return this;
  }

  @Override
  public Socket getProxySocket() {
    return proxySocket;
  }

  @Override
  public Socks5 setProxySocket(Socket proxySocket) {
    this.proxySocket = proxySocket;
    return this;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return proxySocket.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return proxySocket.getOutputStream();
  }

  @Override
  public Socks5 setAcceptableMethods(List<SocksMethod> acceptableMethods) {
    this.acceptableMethods = acceptableMethods;
    return this;
  }

  @Override
  public List<SocksMethod> getAcceptableMethods() {
    return acceptableMethods;
  }

  @Override
  public Socks5 setAuthentication(Authentication authentication) {
    this.authentication = authentication;
    return this;
  }

  @Override
  public Authentication getAuthentication() {
    return authentication;
  }

  @Override
  public Socks5 setSocksMethodRequestor(SocksMethodRequestor requestor) {
    this.socksMethodRequestor = requestor;
    return this;
  }

  @Override
  public SocksMethodRequestor getSocksMethodRequestor() {
    return socksMethodRequestor;
  }

  @Override
  public Socks5 copy() {
    Socks5 socks5 = new Socks5();
    socks5.setAcceptableMethods(acceptableMethods)
        .setAlwaysResolveAddressLocally(alwaysResolveAddressLocally)
        .setAuthentication(authentication).setInetAddress(inetAddress).setPort(port)
        .setSocksMethodRequestor(socksMethodRequestor);
    return socks5;
  }


  @Override
  public int getSocksVersion() {
    return SOCKS_VERSION;
  }

  public Socks5 setHost(String host) throws UnknownHostException {
    inetAddress = InetAddress.getByName(host);
    return this;
  }

  public InetAddress getInetAddress() {
    return inetAddress;
  }

  public Socks5 setInetAddress(InetAddress inetAddress) {
    this.inetAddress = inetAddress;
    return this;
  }

  public boolean isAlwaysResolveAddressLocally() {
    return alwaysResolveAddressLocally;
  }

  public Socks5 setAlwaysResolveAddressLocally(boolean alwaysResolveAddressLocally) {
    this.alwaysResolveAddressLocally = alwaysResolveAddressLocally;
    return this;
  }



  public static final byte SOCKS_VERSION = 0x05;
  public static final byte RESERVED = 0x00;

  public static final int REP_SUCCEEDED = 0x00;
  public static final int REP_GENERAL_SOCKS_SERVER_FAILURE = 0x01;
  public static final int REP_CONNECTION_NOT_ALLOWED_BY_RULESET = 0x02;
  public static final int REP_NETWORK_UNREACHABLE = 0x03;
  public static final int REP_HOST_UNREACHABLE = 0x04;
  public static final int REP_CONNECTION_REFUSED = 0x05;
  public static final int REP_TTL_EXPIRED = 0x06;
  public static final int REP_COMMAND_NOT_SUPPORTED = 0x07;
  public static final int REP_ADDRESS_TYPE_NOT_SUPPORTED = 0x08;

  /**
   * Authentication succeeded code.
   */
  public static final byte AUTHENTICATION_SUCCEEDED = 0x00;
}
