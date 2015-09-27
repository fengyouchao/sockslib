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

package fucksocks.test;

import fucksocks.server.SocksProxyServer;
import fucksocks.server.SocksProxyServerFactory;

import java.io.IOException;

/**
 * The class <code>TestProxyServer</code> a test class to start a SOCKS5 proxy server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Apr 19, 2015 11:43:22 PM
 */
public class TestProxyServer {

  public static void main(String[] args) {

    SocksProxyServer proxyServer = SocksProxyServerFactory.newNoAuthenticationServer();

    try {
      proxyServer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
