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

package fucksocks.common.methods;

import java.io.IOException;

import fucksocks.client.SocksProxy;
import fucksocks.common.SocksException;
import fucksocks.server.Session;


/**
 * The interface <code>SocksMethod</code> define a socks method in SOCKS4 or SOCKS5 protocol.<br>
 * <br>
 * SOCKS5 protocol in RFC 1928:<br>
 * The values currently defined for METHOD are:
 * <ul>
 * <li>X’00’ NO AUTHENTICATION REQUIRED</li>
 * <li>X’01’ GSSAPI</li>
 * <li>X’02’ USERNAME/PASSWORD</li>
 * <li>X’03’ to X’7F’ IANA ASSIGNED</li>
 * <li>X’80’ to X’FE’ RESERVED FOR PRIVATE METHODS</li>
 * <li>X’FF’ NO ACCEPTABLE METHODS</li>
 * </ul>
 * 
 * @author Youchao Feng
 * @date Mar 17, 2015 11:12:16 AM
 * @version 1.0
 * 
 * @see fucksocks.client.AbstractSocksMethod
 * @see fucksocks.client.GssApiMethod
 * @see fucksocks.client.NoAcceptableMethods
 * @see fucksocks.client.NoAuthencationRequiredMethod
 * @see fucksocks.client.UsernamePasswordMethod
 * @see <a href="http://www.ietf.org/rfc/rfc1928.txt">SOCKS Protocol Version 5</a>
 */
public interface SocksMethod {

  /**
   * method byte.
   * 
   * @return byte.
   */
  int getByte();

  /**
   * Gets method's name.
   * 
   * @return Name of the method.
   */
  String getMethodName();

  /**
   * Do method job. This method will be called by SOCKS client.
   * 
   * @param socksProxy SocksProxy instance.
   * @throws SocksException If there are any errors about SOCKS protocol.
   * @throws IOException if there are any IO errors.
   */
  void doMethod(SocksProxy socksProxy) throws SocksException, IOException;

  /**
   * Do method job. This method will be called by SOCKS server.
   * 
   * @param session Session.
   * @throws SocksException TODO
   * @throws IOException TODO
   */
  void doMethod(Session session) throws SocksException, IOException;

}
