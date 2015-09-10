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

import java.io.IOException;

import fucksocks.server.Session;
import fucksocks.server.msg.CommandMessage;

/**
 * The class <code>SocksCommandFilter</code> represents a SOCKS command filter. When a client sends
 * a SOCKS command request message to a SOCKS server, {@link SocksCommandFilter} will intercept the
 * execution process.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date May 10, 2015 10:56:17 AM
 */
public interface SocksCommandFilter {

  /**
   * This method will be called before the SOCKS server do the SOCKS command.
   *
   * @param session Session
   * @param message The message send from a SOKCS client.
   * @return <code>false</code> to block the request or <code>true</code> to continue.
   * @throws IOException If an I/O error occurred. If this method throws any exception, the filter
   *                     will block the client's request.
   */
  boolean doFilter(Session session, CommandMessage message) throws IOException;

}
