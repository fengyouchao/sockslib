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

  void handle(Session session) throws Exception;

  public void doConnect(Session session, CommandMessage commandMessage) throws SocksException,
      IOException;

  public void doBind(Session session, CommandMessage commandMessage) throws SocksException,
      IOException;

  public void doUDPAssociate(Session session, CommandMessage commandMessage) throws SocksException,
      IOException;

  void setSession(Session session);

  public FilterChain getFilterChain();

  public void setFilterChain(FilterChain filterChain);

  public MethodSelector getMethodSelector();

  public void setMethodSelector(MethodSelector methodSelector);

  int getBufferSize();

  void setBufferSize(int bufferSize);

  public List<SocksListener> getSocksListeners();

  public void setSocksListeners(List<SocksListener> socksListeners);

  public int getIdleTime();

  public void setIdleTime(int idleTime);

}
