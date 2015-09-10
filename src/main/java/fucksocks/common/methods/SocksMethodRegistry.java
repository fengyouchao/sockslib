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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class <code>SocksMethodRegistry</code> represents a socks method registry.
 * <p>
 * This class can register some {@link SocksMethod} classes and provide a method {
 * {@link #getByByte(byte)} to return a {@link SocksMethod} class which value is equal the given
 * byte.
 * </p>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 17, 2015 11:12:06 AM
 */
public class SocksMethodRegistry {

  /**
   * Logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(SocksMethodRegistry.class);

  private static final Map<Byte, SocksMethod> methods = new HashMap<Byte, SocksMethod>();

  /**
   * A private constructor.
   */
  private SocksMethodRegistry() {
  }

  /**
   * Puts a {@link SocksMethod} class into the SOCKS method registry with an instance of
   * {@link SocksMethod}.
   *
   * @param socksMethod The instance of {@link SocksMethod}.
   */
  public static void putMethod(SocksMethod socksMethod) {
    logger.debug("Register {}[{}]", socksMethod.getMethodName(), socksMethod.getByte());
    methods.put((byte) socksMethod.getByte(), socksMethod);
  }

  public static void overWriteReistry(List<SocksMethod> socksMethods) {
    for (int i = 0; i < socksMethods.size(); i++) {
      putMethod(socksMethods.get(i));
    }
  }

  /**
   * Returns a {@link SocksMethod} class which value is equal the given byte.
   *
   * @param b value of {@link SocksMethod}.
   * @return A {@link SocksMethod} instance which value is equal the given byte.
   */
  public static SocksMethod getByByte(byte b) {
    return methods.get(b);
  }
}
