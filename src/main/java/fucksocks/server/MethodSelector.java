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

import java.util.Set;

import fucksocks.common.methods.SocksMethod;
import fucksocks.server.msg.MethodSelectionMessage;

/**
 * The class <code>MethodSelector</code> represents a method selector.<br>
 * This class will select one method from the methods that client given. If there is no method
 * acceptable, it will select 0xFF.
 * 
 * @author Youchao Feng
 * @date Apr 7, 2015 10:17:12 AM
 * @version 1.0
 *
 */
public interface MethodSelector {


  /**
   * Selects a method form {@link MethodSelectionMessage}.
   * 
   * @param message the message from client.
   * @return The method that server selected.
   */
  public SocksMethod select(MethodSelectionMessage message);

  /**
   * Sets methods that server supported.
   * 
   * @param supportMethods methods that server supported.
   */
  public void setSupportMethods(Set<SocksMethod> supportMethods);

  /**
   * Gets methods that server supported.
   * 
   * @return The methods that server supported.
   */
  public Set<SocksMethod> getSupportMethods();

  /**
   * Clears all methods that server supported.
   */
  public void clearAllSupportMethods();

  /**
   * Removes the method from the sets.
   * 
   * @param method The method which will be removed.
   */
  public void removeSupportMethod(SocksMethod socksMethod);

  public void addSupportMethod(SocksMethod socksMethod);

  public void setSupportMethod(SocksMethod... methods);


}
