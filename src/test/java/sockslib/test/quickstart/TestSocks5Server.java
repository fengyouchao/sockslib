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

package sockslib.test.quickstart;

import sockslib.client.SSLSocks5;
import sockslib.client.Socks5;
import sockslib.client.SocksProxy;
import sockslib.common.SSLConfiguration;
import sockslib.common.UsernamePasswordCredentials;
import sockslib.quickstart.Socks5Server;
import sockslib.utils.TCPTelnet;
import sockslib.utils.Telnet;
import sockslib.utils.UDPTelnet;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static socklib.test.SSLResource.PASSWORD;
import static socklib.test.SSLResource.getClientSSLConfigPath;
import static socklib.test.SSLResource.getServerKeyStorePath;
import static socklib.test.SSLResource.getServerSSLConfigPath;

/**
 * The class <code>TestSocks5Server</code> is the test class for {@link Socks5Server}.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 9:07 AM
 */
public final class TestSocks5Server {

  private final int REMOTE_TCP_SERVER_PORT = 8888;
  private final int REMOTE_UDP_SERVER_PORT = 8889;
  private final SocketAddress socksServerAddress = new InetSocketAddress("127.0.0.1", 1080);
  private final SocketAddress remoteTCPServerAddress =
      new InetSocketAddress("127.0.0.1", REMOTE_TCP_SERVER_PORT);
  private final SocketAddress remoteUDPServerAddress =
      new InetSocketAddress("127.0.0.1", REMOTE_UDP_SERVER_PORT);
  private Socks5Server socks5Server;

  @Before
  public void init() throws FileNotFoundException {
    socks5Server = new Socks5Server();
  }

  @Test
  public void testNoAuthConnect() throws IOException {
    startNoAuthSocks5Server();
    checkConnect(getSocks5());
  }

  @Test
  public void testUsernamePasswordAuthConnect() throws IOException {
    startAuthSocks5Server();
    SocksProxy proxy = getSocks5();
    proxy.setCredentials(new UsernamePasswordCredentials("admin", "12345"));
    checkConnect(proxy);
  }

  @Test
  public void testSSLConnect() throws IOException {
    startNoAuthSSLSocks5Server();
    checkConnect(getSSLSocks5());
  }

  @Test
  public void testSSL2Connect() throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    String keystorePassword = PASSWORD;
    String value = String.format("-p 1080 -k %s -w %s", getServerKeyStorePath(), keystorePassword);
    socks5Server.start(builder.addArguments(value).build());
    checkConnect(getSSLSocks5());
  }

  @Test
  public void testNoAuthUDP() throws IOException {
    startNoAuthSocks5Server();
    checkUDPAssociate(getSocks5());
  }

  @Test
  public void testAuthUDP() throws IOException {
    startAuthSocks5Server();
    SocksProxy proxy = getSocks5();
    proxy.setCredentials(new UsernamePasswordCredentials("admin", "12345"));
    checkUDPAssociate(proxy);
  }

  @Test
  public void tesSslUDP() throws IOException {
    startNoAuthSSLSocks5Server();
    checkUDPAssociate(getSSLSocks5());
  }

  @After
  public void destroy() throws InterruptedException {
    socks5Server.shutdown();
    Thread.sleep(100);
  }

  private void startNoAuthSocks5Server() throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    builder.addArguments("-port 1080");
    socks5Server.start(builder.build());
  }

  private void startAuthSocks5Server() throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    builder.addArguments("-port 1080 --auth admin:12345");
    socks5Server.start(builder.build());
  }

  public void startNoAuthSSLSocks5Server() throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    builder.addArguments("--port 1080").addArgument("--ssl").addArgument(getServerSSLConfigPath());
    socks5Server.start(builder.build());
  }

  public void startAuthSSLSocks5Server() throws IOException {
  }

  private void checkConnect(SocksProxy proxy) throws IOException {
    SampleTCPServer server = new SampleTCPServer();
    server.start(REMOTE_TCP_SERVER_PORT);
    Telnet telnet = new TCPTelnet(proxy);
    String requestMessage = "hello fucksocks\n";
    byte[] data = telnet.request(requestMessage.getBytes(), remoteTCPServerAddress);
    server.shutdown();
    Assert.assertEquals(requestMessage, new String(data));
  }

  private void checkUDPAssociate(SocksProxy proxy) throws IOException {
    SampleUDPServer server = new SampleUDPServer();
    server.start(REMOTE_UDP_SERVER_PORT);
    Telnet telnet = new UDPTelnet(proxy);
    String requestMessage = "hello fucksocks\n";
    byte[] data = telnet.request(requestMessage.getBytes(), remoteUDPServerAddress);
    server.shutdown();
    Assert.assertEquals(requestMessage, new String(data));
  }

  private SocksProxy getSSLSocks5() throws IOException {
    SSLConfiguration sslConfiguration = SSLConfiguration.load(getClientSSLConfigPath());
    return new SSLSocks5(socksServerAddress, sslConfiguration);
  }

  private SocksProxy getSocks5() {
    return new Socks5(socksServerAddress);
  }
}
