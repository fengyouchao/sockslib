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
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import fucksocks.common.Authentication;
import fucksocks.common.SocksException;
import fucksocks.common.methods.SocksMethod;

/**
 * The interface <code>SocksProxy</code> define a SOCKS proxy. it's will be used by
 * {@link SocksSocket} or {@link Socks5DatagramSocket}
 * 
 * @author Youchao Feng
 * @date Mar 18, 2015 3:29:18 PM
 * @version 1.0
 */
public interface SocksProxy {

  /**
   * Get the socket which connect SOCKS server.
   * 
   * @return java.net.Socket.
   */
  Socket getProxySocket();


  /**
   * Get SOCKS Server port.
   * 
   * @return server port.
   */
  int getPort();

  /**
   * Set SOCKS server port.
   * 
   * @param port SOCKS server's port.
   * @return instance of SocksProxy.
   */
  SocksProxy setPort(int port);

  /**
   * Get SOCKS server's address as IPv4 or IPv6.
   * 
   * @return server's IP address.
   */
  InetAddress getInetAddress();

  /**
   * Set SOCKS server's host.
   * 
   * @param host SOCKS server's host.
   * @return instance of SocksProxy.
   * @throws UnknownHostException if host can't resolve to {@link java.net.InetAddress}
   */
  SocksProxy setHost(String host) throws UnknownHostException;

  /**
   * Set a unconnected socket which will be used to connect SOCKS server.
   * 
   * @param socket a unconnected socket.
   * @return instance of SocksProxy.
   */
  SocksProxy setProxySocket(Socket socket);

  /**
   * Connect SOCKS server using SOCKS protocol.<br>
   * 
   * <p>
   * This method will ask SOCKS server to select a method from the methods listed by client. If
   * SOCKS server need authentication, it will do authentication. If SOCKS server select 0xFF,It
   * means that none of the methods listed by the client are acceptable and this method should throw
   * {@link SocksException}.
   * </p>
   * 
   * @throws IOException if any IO error occurs.
   * @throws SocksException if any error about SOCKS protocol occurs.
   * 
   */
  void buildConnection() throws IOException, SocksException;

  /**
   * This method will send a CONNECT command to SOCKS server and ask SOCKS server to connect remote
   * server.
   * 
   * @param host Remote server's host.
   * @param port Remote server's port.
   * @return The message that reply by SOCKS server.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException if any I/O error occurs.
   */
  CommandReplyMesasge reqeustConnect(String host, int port) throws SocksException, IOException;

  /**
   * This method will send a CONNECT command to SOCKS server and ask SOCKS server to connect remote
   * server.
   * 
   * @param address Remote server's address as java.net.InetAddress instance.
   * @param port Remote server's port.
   * @return The message that reply by SOCKS server.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException If any I/O error occurs.
   */
  CommandReplyMesasge reqeustConnect(InetAddress address, int port) throws SocksException,
      IOException;

  /**
   * This method will send a CONNECT command to SOCKS server and ask SOCKS server to connect remote
   * server.
   * 
   * @param address Remote server's address as java.net.SocketAddress instance.
   * @return The message that reply by SOCKS server.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException If any I/O error occurs.
   */
  CommandReplyMesasge reqeustConnect(SocketAddress address) throws SocksException, IOException;

  /**
   * This method will send a BIND command to SOKCS server.
   * 
   * @param host Remote server's host.
   * @param port Remote server's port.
   * @return The message that reply by SOCKS server.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException If any I/O error occurs.
   */
  public CommandReplyMesasge requestBind(String host, int port) throws SocksException, IOException;


  /**
   * This method will send a BIND command to SOKCS server.
   * 
   * @param inetAddress Remote server's IP address.
   * @param port Remote server's port.
   * @return The message that reply by SOCKS server.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException If any I/O error occurs.
   */
  public CommandReplyMesasge requestBind(InetAddress inetAddress, int port) throws SocksException,
      IOException;

  /**
   * When binded server has income connection, this method will read second response message from
   * SOCKS server. <br>
   * This method will be blocked if there is no income connection. When there is a income
   * connection, this method will return a socket that looks like connect the remote host.
   * 
   * @return Socket that connect the remote host.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException If any I/O error occurs.
   */
  Socket accept() throws SocksException, IOException;

  /**
   * This method will send a UDP ASSOCIAT command to SOCKS server and ask SOCKS server to establish
   * a relay server.
   * 
   * @param host Remote UDP server's host.
   * @param port Remote UDP server's port.
   * @return The message that reply by SOCKS server.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException If any I/O error occurs.
   */
  CommandReplyMesasge requestUdpAssociat(String host, int port) throws SocksException, IOException;

  /**
   * This method will send a UDP ASSOCIAT command to SOCKS server and ask SOCKS server to establish
   * a relay server.
   * 
   * @param address Remote UDP server's address.
   * @param port Remote UDP server's port.
   * @return The message that reply by SOCKS server.
   * @throws SocksException If any error about SOCKS protocol occurs.
   * @throws IOException If any I/O error occurs.
   */
  CommandReplyMesasge requestUdpAssociat(InetAddress address, int port) throws SocksException,
      IOException;

  /**
   * Gets InputStream from the socket that connected SOCKS server.
   * 
   * @return java.net.InputStream.
   * @throws IOException if any I/O error occurs.
   */
  InputStream getInputStream() throws IOException;

  /**
   * Gets OutputStream from the socket that connected SOCKS server.
   * 
   * @return java.net.OutputStream.
   * @throws IOException if any I/O error occurs.
   */
  OutputStream getOutputStream() throws IOException;

  /**
   * Sets Authentication.
   * 
   * @param authentication {@link Authentication} instance.
   * @return instance of SocksProxy.
   * 
   * @see fucksocks.common.UsernamePasswordAuthencation
   * @see fucksocks.common.AnonymousAuthentication
   */
  SocksProxy setAuthentication(Authentication authentication);

  /**
   * Gets Authentication instance from the SocksProxy.
   * 
   * @return {@link Authentication} instance.
   */
  Authentication getAuthentication();

  /**
   * Sets client's acceptable methods.
   * 
   * @param methods methods.
   * @return instance of SocksProxy.
   */
  SocksProxy setAcceptableMethods(List<SocksMethod> methods);

  /**
   * Gets clent's acceptable methods.
   * 
   * @return clent's acceptable methods.
   */
  List<SocksMethod> getAcceptableMethods();

  /**
   * Sets {@link SocksMethodRequestor}.
   * 
   * @param requestor {@link SocksMethodRequestor}
   * @return instance of SocksProxy.
   */
  SocksProxy setSocksMethodRequestor(SocksMethodRequestor requestor);

  /**
   * Gets {@link SocksMethodRequestor}.
   * 
   * @return {@link SocksMethodRequestor}.
   */
  SocksMethodRequestor getSocksMethodRequestor();

  /**
   * Gets version of SOCKS protocol.
   * 
   * @return Version of SOCKS protocol.
   */
  int getSocksVersion();


  /**
   * This method can create a same SocksProxy instance.
   * 
   * <p>
   * The new instance created by this method has the same properties with the original instance, but
   * they have different socket instance. The new instance's socket is also unconnected.
   * </p>
   * 
   * @return The copy of this SocksProxy.
   */
  SocksProxy copy();

  /**
   * Default SOCKS server port.
   */
  public static final int SOCKS_DEFAULT_PORT = 1080;

  public static final byte ATYPE_IPV4 = 0x01;
  public static final byte ATYPE_DOMAINNAME = 0x03;
  public static final byte ATYPE_IPV6 = 0x04;

}
