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

import java.io.IOException;
import java.util.Map;

import fucksocks.common.methods.SocksMethod;
import fucksocks.server.filters.SocksListener;

/**
 * The interface <code>SocksProxyServer</code> represents a SOCKS server.
 * 
 * @author Youchao Feng
 * @date Mar 25, 2015 10:07:29 AM
 * @version 1.0
 */
public interface SocksProxyServer {

  /**
   * Starts a SOCKS server bind a default port.
   * 
   * @throws IOException If any I/O error occurs.
   */
  public void start() throws IOException;

  /**
   * Starts a SOCKS server and binds a port.
   * 
   * @param bindPort The port that SOCKS server listened..
   * @throws IOException If any I/O error occurs.
   */
  public void start(int bindPort) throws IOException;

  /**
   * Shutdown a SOCKS server.
   */
  public void shutdown();


  public SocksHandler createSocksHandler();

  /**
   * Initializes {@link SocksHandler}.
   * 
   * @param socksHandler The instance of {@link SocksHandler}.
   */
  public void initializeSocksHandler(SocksHandler socksHandler);

  public void setSupportedMethod(SocksMethod... methods);

  public Map<Long, Session> getManagedSessions();

  public void setBufferSize(int bufferSize);

  public int getBufferSize();

  public int getTimeout();

  public void setTimeout(int timeout);

  public void removeSocksListenner(SocksListener socksListener);

  public void addSocksListenner(SocksListener socksListener);


  public static final int DEFAULT_SOCKS_PORT = 1080;

}
