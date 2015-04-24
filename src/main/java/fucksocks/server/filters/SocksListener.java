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

package fucksocks.server.filters;

import fucksocks.server.Session;
import fucksocks.server.msg.CommandMessage;

/**
 * The class <code>SocksListener</code> represents a listener that can listen some event of SOCKS
 * server.
 * 
 * @author Youchao Feng
 * @date Apr 22, 2015 12:19:19 AM
 * @version 1.0
 *
 */
public interface SocksListener {

  public void onSessionCreated(Session session);

  public void onCommandReceived(Session session, CommandMessage message);

}
