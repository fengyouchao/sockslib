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

/**
 * 
 * The class <code>SessionFilter</code> represents a session filter.
 * 
 * @author Youchao Feng
 * @date May 2, 2015 12:36:44 AM
 * @version 1.0
 *
 */
public interface SessionFilter {

  /**
   * Return true, the {@link SessionFilterChain} will call the next SessionFilter if it has next
   * SessionFilter. It will break the process if return false.
   * 
   * @param session Session between client and server.
   * @return <code>false</code> to break the process or <code>true<code> to continue.
   */
  public boolean doFilter(Session session);

}
