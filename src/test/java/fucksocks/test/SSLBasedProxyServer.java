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

import fucksocks.common.SSLConfiguration;
import fucksocks.server.SocksProxyServer;
import fucksocks.server.SocksServerBuilder;
import fucksocks.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The class <code>SSLBasedProxyServer</code> is a test class to start a SSL based SOCKS5 proxy
 * server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @since 1.0
 */
public class SSLBasedProxyServer {

  private static final Logger logger = LoggerFactory.getLogger(SSLBasedProxyServer.class);

  public static void main(String[] args) throws IOException {
    Timer.open();
    SSLConfiguration configuration = SSLConfiguration.loadClassPath("server-ssl.properties");
    SocksProxyServer proxyServer = SocksServerBuilder.buildAnonymousSSLSocks5Server(1081, configuration);
    try {
      proxyServer.start();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

}
