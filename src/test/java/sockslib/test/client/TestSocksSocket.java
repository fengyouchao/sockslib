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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import socklib.test.Ports;
import socklib.test.UnitPort;
import sockslib.client.Socks5;
import sockslib.client.SocksProxy;
import sockslib.client.SocksSocket;
import sockslib.common.AuthenticationException;
import sockslib.common.UsernamePasswordCredentials;
import sockslib.common.methods.NoAuthenticationRequiredMethod;
import sockslib.common.methods.UsernamePasswordMethod;
import sockslib.server.SocksProxyServer;
import sockslib.server.SocksServerBuilder;
import sockslib.server.UsernamePasswordAuthenticator;
import sockslib.server.manager.MemoryBasedUserManager;
import sockslib.server.manager.User;
import sockslib.server.manager.UserManager;

import java.io.IOException;

/**
 * The class <code>TestSocketSocket</code> is a test class for {@link SocksSocket}
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 2:57 PM
 */
public final class TestSocksSocket {

  private static final String username = "admin";
  private static final String password = "12345";
  private SocksProxyServer socksProxyServer;

  @Rule
  public final UnitPort port = new UnitPort();

  @After
  public void destroy() throws InterruptedException {
    socksProxyServer.shutdown();
    Thread.sleep(100);
  }

  @Test
  public void testNoAuth() throws IOException {
    startNoAuthSocks5Server();
    SocksProxy proxy = new Socks5(Ports.localSocketAddress(port.get()));
    SocksTester.checkConnect(proxy);
  }

  @Test
  public void testUsernamePasswordAuth() throws IOException {
    startAuthSocks5Server();
    SocksProxy proxy = new Socks5(Ports.localSocketAddress(port.get()));
    proxy.setCredentials(new UsernamePasswordCredentials(username, password));
    SocksTester.checkConnect(proxy);
  }

  @Test(expected = AuthenticationException.class)
  public void testUsernamePasswordAuthFailed() throws IOException {
    startAuthSocks5Server();
    SocksProxy proxy = new Socks5(Ports.localSocketAddress(port.get()));
    proxy.setCredentials(new UsernamePasswordCredentials(username, "wrong password"));
    SocksTester.checkConnect(proxy);
  }

  @Test(timeout = 5000)
  public void testProxyChain() throws IOException {
    int port1 = port.get(), port2 = Ports.unused(), port3 = Ports.unused();
    SocksProxyServer server1 = SocksServerBuilder.buildAnonymousSocks5Server(port2);
    SocksProxyServer server2 = SocksServerBuilder.buildAnonymousSocks5Server(port3);
    startNoAuthSocks5Server();
    server1.start();
    server2.start();
    SocksProxy proxy = new Socks5("127.0.0.1", port1);
    SocksProxy proxy1 = new Socks5("127.0.0.1", port2);
    SocksProxy proxy2 = new Socks5("127.0.0.1", port3);
    proxy.setChainProxy(proxy1);
    proxy1.setChainProxy(proxy2);
    SocksTester.checkConnect(proxy);
    server1.shutdown();
    server2.shutdown();
    System.out.println(proxy);
  }

  private void startNoAuthSocks5Server() throws IOException {
    SocksServerBuilder builder = SocksServerBuilder.newSocks5ServerBuilder();
    builder.setBindPort(port.get())
            .setSocksMethods(new NoAuthenticationRequiredMethod())
        .setDaemon(true);
    socksProxyServer = builder.build();
    socksProxyServer.start();
  }

  private void startAuthSocks5Server() throws IOException {
    UserManager userManager = new MemoryBasedUserManager();
    userManager.create(new User(username, password));
    SocksServerBuilder builder = SocksServerBuilder.newSocks5ServerBuilder();
    builder.setBindPort(port.get())
            .setSocksMethods(new UsernamePasswordMethod(new UsernamePasswordAuthenticator(userManager)));
    socksProxyServer = builder.build();
    socksProxyServer.start();
  }

}
