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

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import socklib.test.Ports;
import socklib.test.UnitPort;
import sockslib.client.SSLSocks5;
import sockslib.client.Socks5;
import sockslib.client.SocksProxy;
import sockslib.common.SSLConfiguration;
import sockslib.common.UsernamePasswordCredentials;
import sockslib.quickstart.Socks5Server;
import sockslib.utils.TCPTelnet;
import sockslib.utils.Telnet;
import sockslib.utils.UDPTelnet;

import java.io.FileNotFoundException;
import java.io.IOException;

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

  private Socks5Server socks5Server;

  @Rule
  public UnitPort port = new UnitPort();

  @Rule
  public Timeout globalTimeout = new Timeout(5 * 1000);

  @Before
  public void init() throws FileNotFoundException {
    socks5Server = new Socks5Server();
  }

  @Test
  public void testNoAuthConnect() throws IOException {
    startNoAuthSocks5Server(port.get());
    checkConnect(getSocks5());
  }

  @Test
  public void testUsernamePasswordAuthConnect() throws IOException {
    startAuthSocks5Server(port.get(), "admin", "12345");
    SocksProxy proxy = getSocks5();
    proxy.setCredentials(new UsernamePasswordCredentials("admin", "12345"));
    checkConnect(proxy);
  }

  @Test
  public void testSSLConnect() throws IOException {
    startNoAuthSSLSocks5Server(port.get());
    checkConnect(getSSLSocks5());
  }

  @Test
  public void testSSL2Connect() throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    String keystorePassword = PASSWORD;
    String value = String.format("-p %d -k %s -w %s", port.get(), getServerKeyStorePath(), keystorePassword);
    socks5Server.start(builder.addArguments(value).build());
    checkConnect(getSSLSocks5());
  }

  @Test
  public void testNoAuthUDP() throws IOException {
    startNoAuthSocks5Server(port.get());
    checkUDPAssociate(getSocks5());
  }

  @Test
  public void testAuthUDP() throws IOException {
    startAuthSocks5Server(port.get(), "admin", "12345");
    SocksProxy proxy = getSocks5();
    proxy.setCredentials(new UsernamePasswordCredentials("admin", "12345"));
    checkUDPAssociate(proxy);
  }

  @Test
  public void tesSslUDP() throws IOException {
    startNoAuthSSLSocks5Server(port.get());
    checkUDPAssociate(getSSLSocks5());
  }

  @After
  public void destroy() throws InterruptedException {
    socks5Server.shutdown();
    Thread.sleep(100);
  }

  private void startNoAuthSocks5Server(int port) throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    builder.addArguments("--port " + port);
    socks5Server.start(builder.build());
  }

  private void startAuthSocks5Server(int port, String username, String password) throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    builder.addArguments(String.format("--port %d --auth %s:%s", port, username, password));
    socks5Server.start(builder.build());
  }

  public void startNoAuthSSLSocks5Server(int port) throws IOException {
    ArgumentsBuilder builder = ArgumentsBuilder.newBuilder();
    builder.addArguments("--port " + port).addArgument("--ssl").addArgument(getServerSSLConfigPath());
    socks5Server.start(builder.build());
  }

  public void startAuthSSLSocks5Server() throws IOException {
  }

  private void checkConnect(SocksProxy proxy) throws IOException {
    SampleTCPServer server = new SampleTCPServer();
    int tcpServerPort = Ports.unused();
    server.start(tcpServerPort);
    Telnet telnet = new TCPTelnet(proxy);
    String requestMessage = "hello fucksocks\n";
    byte[] data = telnet.request(requestMessage.getBytes(), Ports.localSocketAddress(tcpServerPort));
    server.shutdown();
    Assert.assertEquals(requestMessage, new String(data));
  }

  private void checkUDPAssociate(SocksProxy proxy) throws IOException {
    SampleUDPServer server = new SampleUDPServer();
    int udpServerPort = Ports.unused();
    server.start(udpServerPort);
    Telnet telnet = new UDPTelnet(proxy);
    String requestMessage = "hello fucksocks\n";
    byte[] data = telnet.request(requestMessage.getBytes(), Ports.localSocketAddress(udpServerPort));
    server.shutdown();
    Assert.assertEquals(requestMessage, new String(data));
  }

  private SocksProxy getSSLSocks5() throws IOException {
    SSLConfiguration sslConfiguration = SSLConfiguration.load(getClientSSLConfigPath());
    return new SSLSocks5(Ports.localSocketAddress(port.get()), sslConfiguration);
  }

  private SocksProxy getSocks5() throws IOException {
    return new Socks5(Ports.localSocketAddress(port.get()));
  }
}
