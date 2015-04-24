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
 * The class <code>NoAcceptableMethod</code> represents a method which indicates none of the methods
 * listed by the client are acceptable.
 * <p>
 * When server replies this method, the client will disconnect SOCKS server and throw
 * {@link SocksException}.
 * </p>
 * 
 * @author Youchao Feng
 * @date Mar 18, 2015 11:15:46 AM
 * @version 1.0
 * 
 * @see AbstractSocksMethod
 * @see <a href="http://www.ietf.org/rfc/rfc1928.txt">SOCKS Protocol Version 5</a>
 */
public class NoAcceptableMethod extends AbstractSocksMethod {

  @Override
  public final int getByte() {
    return 0xFF;
  }

  @Override
  public void doMethod(SocksProxy socksProxy) throws SocksException, IOException {
    // Close socket and throw SocksException.
    if (!socksProxy.getProxySocket().isClosed()) {
      socksProxy.getProxySocket().close();
    }
    throw SocksException.noAcceptableMethods();
  }

  @Override
  public void doMethod(Session session) throws SocksException, IOException {
    session.close();
    throw SocksException.noAcceptableMethods();
  }

  @Override
  public String getMethodName() {
    return "No Acceptable Method";
  }

}
