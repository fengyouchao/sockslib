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

package fucksocks.common;

/**
 * The class <code>AddressType</code> represents type of address.
 * 
 * @author Youchao Feng
 * @date Apr 24, 2015 8:21:31 PM
 * @version 1.0
 *
 */
public class AddressType {

  public static final int IPV4 = 0x01;
  public static final int DOMAINNAME = 0x03;
  public static final int IPV6 = 0x04;

  private AddressType() {}

  public static boolean isSupport(int type) {
    return type == IPV4 || type == DOMAINNAME || type == IPV6;
  }

}
