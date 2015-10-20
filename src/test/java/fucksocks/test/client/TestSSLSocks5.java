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

package fucksocks.test.client;

import fucksocks.client.SSLSocks5;
import fucksocks.client.SocksProxy;
import fucksocks.common.SSLConfigurationBuilder;
import fucksocks.server.SocksProxyServer;
import fucksocks.server.SocksServerBuilder;
import fucksocks.test.SSLResource;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static fucksocks.test.SSLResource.PASSWORD;
import static fucksocks.test.SSLResource.getClientTrustStorePath;
import static fucksocks.test.SSLResource.getServerKeyStorePath;

/**
 * The class <code>TestSSLSocks5</code> is a test class for {@link SSLSocks5}.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 4:13 PM
 */
public final class TestSSLSocks5 {

  private final int PORT = 1080;
  private SocksProxyServer socksProxyServer;
  private SocketAddress socks5ServerAddress = new InetSocketAddress("127.0.0.1", 1080);



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
    builder.setTrustKeyStorePath(getClientTrustStorePath());
    builder.setTrustKeyStorePassword(PASSWORD);
    SocksProxy proxy = new SSLSocks5(socks5ServerAddress, builder.build());
    SocksTester.checkConnect(proxy);
  }


  @Test
  public void testConnectSSLAuthServer() throws IOException {
    startNoAuthSSLServer();
    SSLConfigurationBuilder builder = SSLConfigurationBuilder.newBuilder();
    builder.setTrustKeyStorePath(getClientTrustStorePath()).setTrustKeyStorePassword(PASSWORD)
        .setKeyStorePath(SSLResource.getClientKeyStorePath()).setKeyStorePassword(PASSWORD);
    SocksProxy proxy = new SSLSocks5(socks5ServerAddress, builder.build());
    SocksTester.checkConnect(proxy);
  }

  public void startNoAuthSSLServer() throws IOException {
    SSLConfigurationBuilder builder = SSLConfigurationBuilder.newBuilder();
    builder.setClientAuth(false);
    builder.setKeyStorePath(getServerKeyStorePath());
    builder.setKeyStorePassword(PASSWORD);
    socksProxyServer = SocksServerBuilder.buildAnonymousSSLSocks5Server(PORT, builder.build());
    socksProxyServer.start();
  }

  private void startSSLServerWithClientAuthBySSL() throws IOException {
    SSLConfigurationBuilder builder = SSLConfigurationBuilder.newBuilder();
    builder.setClientAuth(false);
    builder.setKeyStorePath(getServerKeyStorePath());
    builder.setKeyStorePassword(PASSWORD);
    builder.setTrustKeyStorePath(SSLResource.getServerTrustStorePath());
    builder.setTrustKeyStorePassword(PASSWORD);
    builder.setClientAuth(true);
    socksProxyServer = SocksServerBuilder.buildAnonymousSSLSocks5Server(PORT, builder.build());
    socksProxyServer.start();
  }

}
