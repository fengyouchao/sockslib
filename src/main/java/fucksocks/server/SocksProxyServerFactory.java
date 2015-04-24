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

import fucksocks.common.methods.NoAuthencationRequiredMethod;
import fucksocks.common.methods.UsernamePasswordMethod;

/**
 * The class <code>SocksProxyServerFactory</code> is a factory class for socks server.
 * 
 * @author Youchao Feng
 * @date Apr 19, 2015 10:41:59 PM
 * @version 1.0
 *
 */
public class SocksProxyServerFactory {

  /**
   * Default timeout.
   */
  private static final int TIMEOUT = 100000;

  /**
   * Default buffer size;
   */
  private static final int BUFFER_SIZE = 1024 * 1024 * 5;

  public static SocksProxyServer newNoAuthenticaionServer() {
    SocksProxyServer proxyServer = new GenericSocksProxyServer(Socks5Handler.class);
    proxyServer.setBufferSize(BUFFER_SIZE);
    proxyServer.setTimeout(TIMEOUT);
    proxyServer.setSupportedMethod(new NoAuthencationRequiredMethod());
    return proxyServer;
  }

  public static SocksProxyServer newUsernamePasswordAutenticationServer(User... users) {
    SocksProxyServer proxyServer = new GenericSocksProxyServer(Socks5Handler.class);
    proxyServer.setBufferSize(BUFFER_SIZE);
    proxyServer.setTimeout(TIMEOUT);
    UsernamePasswordAuthenticator authenticator = new UsernamePasswordAuthenticator();
    for (User user : users) {
      authenticator.addUser(user.getUsername(), user.getPassword());
    }
    proxyServer.setSupportedMethod(new UsernamePasswordMethod(authenticator));
    return proxyServer;
  }

}
