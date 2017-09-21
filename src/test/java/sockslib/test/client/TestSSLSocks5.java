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

package sockslib.test.client;

import org.junit.Rule;
import socklib.test.Ports;
import socklib.test.UnitPort;
import sockslib.client.SSLSocks5;
import sockslib.client.SocksProxy;
import sockslib.common.SSLConfigurationBuilder;
import sockslib.server.SocksProxyServer;
import sockslib.server.SocksServerBuilder;
import socklib.test.SSLResource;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * The class <code>TestSSLSocks5</code> is a test class for {@link SSLSocks5}.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 4:13 PM
 */
public final class TestSSLSocks5 {

  private SocksProxyServer socksProxyServer;

  @Rule
  public final UnitPort port = new UnitPort();

  @After
  public void destroy() {
    if (socksProxyServer != null) {
      socksProxyServer.shutdown();
    }
  }

  @Test
  public void testConnectNoAuthSSLServer() throws IOException {
    startNoAuthSSLServer();
    SSLConfigurationBuilder builder = SSLConfigurationBuilder.newBuilder();
    builder.setTrustKeyStorePath(SSLResource.getClientTrustStorePath());
    builder.setTrustKeyStorePassword(SSLResource.PASSWORD);
    SocksProxy proxy = new SSLSocks5(Ports.localSocketAddress(port.get()), builder.build());
    SocksTester.checkConnect(proxy);
  }


  @Test
  public void testConnectSSLAuthServer() throws IOException {
    startNoAuthSSLServer();
    SSLConfigurationBuilder builder = SSLConfigurationBuilder.newBuilder();
    builder.setTrustKeyStorePath(SSLResource.getClientTrustStorePath()).setTrustKeyStorePassword(
        SSLResource.PASSWORD)
        .setKeyStorePath(SSLResource.getClientKeyStorePath()).setKeyStorePassword(
        SSLResource.PASSWORD);
    SocksProxy proxy = new SSLSocks5(Ports.localSocketAddress(port.get()), builder.build());
    SocksTester.checkConnect(proxy);
  }

  public void startNoAuthSSLServer() throws IOException {
    SSLConfigurationBuilder builder = SSLConfigurationBuilder.newBuilder();
    builder.setClientAuth(false);
    builder.setKeyStorePath(SSLResource.getServerKeyStorePath());
    builder.setKeyStorePassword(SSLResource.PASSWORD);
    socksProxyServer = SocksServerBuilder.buildAnonymousSSLSocks5Server(port.get(), builder.build());
    socksProxyServer.start();
  }

  private void startSSLServerWithClientAuthBySSL() throws IOException {
    SSLConfigurationBuilder builder = SSLConfigurationBuilder.newBuilder();
    builder.setClientAuth(false);
    builder.setKeyStorePath(SSLResource.getServerKeyStorePath());
    builder.setKeyStorePassword(SSLResource.PASSWORD);
    builder.setTrustKeyStorePath(SSLResource.getServerTrustStorePath());
    builder.setTrustKeyStorePassword(SSLResource.PASSWORD);
    builder.setClientAuth(true);
    socksProxyServer = SocksServerBuilder.buildAnonymousSSLSocks5Server(port.get(), builder.build());
    socksProxyServer.start();
  }

}
