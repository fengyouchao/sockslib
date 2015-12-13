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

package sockslib.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sockslib.server.SessionManager;
import sockslib.server.SocksProxyServer;
import sockslib.server.SocksServerBuilder;
import sockslib.server.listener.LoggingListener;
import sockslib.utils.Timer;

import java.io.IOException;

/**
 * The class <code>AnonymousSocks5Server</code> a test class to start a SOCKS5 proxy server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Apr 19, 2015 11:43:22 PM
 */
public class AnonymousSocks5Server {

  private static final Logger logger = LoggerFactory.getLogger(AnonymousSocks5Server.class);

  public static void main(String[] args) throws IOException {
    Timer.open();
    SocksProxyServer proxyServer = SocksServerBuilder.buildAnonymousSocks5Server();
    SessionManager sessionManager = proxyServer.getSessionManager();
    sessionManager.addSessionListener("logging", new LoggingListener());
    try {
      proxyServer.start();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }
}

