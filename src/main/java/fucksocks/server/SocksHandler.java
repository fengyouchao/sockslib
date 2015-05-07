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
import java.util.List;

import fucksocks.common.SocksException;
import fucksocks.server.filters.FilterChain;
import fucksocks.server.filters.SocksListener;
import fucksocks.server.msg.CommandMessage;

/**
 * The interface <code>SocksHandler</code> represents a socket handler.
 * 
 * @author Youchao Feng
 * @date Mar 25, 2015 11:33:28 AM
 * @version 1.0
 */
public interface SocksHandler extends Runnable {

  /**
   * Handles a session.
   * 
   * @param session Session.
   * @throws Exception If any error occurred.
   */
  void handle(Session session) throws Exception;

  /**
   * Do CONNECTE command.
   * 
   * @param session Session
   * @param commandMessage {@link CommandMessage} read from SOCKS client.
   * @throws SocksException If a SOCKS protocol error occurred.
   * @throws IOException If a I/O error occurred.
   */
  public void doConnect(Session session, CommandMessage commandMessage) throws SocksException,
      IOException;

  /**
   * Do BIND command.
   * 
   * @param session Session.
   * @param commandMessage {@link CommandMessage} read from SOCKS client.
   * @throws SocksException If a SOCKS protocol error occurred.
   * @throws IOException If a I/O error occurred.
   */
  public void doBind(Session session, CommandMessage commandMessage) throws SocksException,
      IOException;

  /**
   * Do UDP ASSOCIATE command.
   * 
   * @param session Session.
   * @param commandMessage {@link CommandMessage} read from SOCKS client.
   * @throws SocksException If a SOCKS protocol error occurred.
   * @throws IOException If a I/O error occurred.
   */
  public void doUDPAssociate(Session session, CommandMessage commandMessage) throws SocksException,
      IOException;

  /**
   * Sets session.
   * 
   * @param session Session.
   */
  void setSession(Session session);

  /**
   * Returns filter chain.
   * 
   * @return Filter chain.
   */
  public FilterChain getFilterChain();

  /**
   * Sets filter chain.
   * 
   * @param filterChain Filter chain.
   */
  public void setFilterChain(FilterChain filterChain);

  /**
   * Returns method selector.
   * 
   * @return Method selector.
   */
  public MethodSelector getMethodSelector();

  /**
   * Sets a method selector.
   * 
   * @param methodSelector A {@link MethodSelector} instance.
   */
  public void setMethodSelector(MethodSelector methodSelector);

  /**
   * Returns buffer size.
   * 
   * @return Buffer size.
   */
  int getBufferSize();

  /**
   * Sets buffer size.
   * 
   * @param bufferSize buffer size.
   */
  void setBufferSize(int bufferSize);

  /**
   * Returns all socks listeners.
   * 
   * @return All socks listeners.
   */
  public List<SocksListener> getSocksListeners();

  /**
   * Sets socks listeners.
   * 
   * @param socksListeners List of {@link SocksListener}.
   */
  public void setSocksListeners(List<SocksListener> socksListeners);

  /**
   * Returns idle time.
   * 
   * @return idle time.
   */
  public int getIdleTime();

  /**
   * Sets idle time.
   * 
   * @param idleTime Idle time.
   */
  public void setIdleTime(int idleTime);

}
