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
 * @date Mar 17, 2015 11:12:06 AM
 * @version 1.0
 * 
 */
public class SocksMethodRegistry {

  /**
   * Logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(SocksMethodRegistry.class);

  private static final Map<Byte, Class<? extends SocksMethod>> methodsMap;

  /**
   * Initialize.
   */
  static {
    methodsMap = new HashMap<>();
    putMethod(NoAuthencationRequiredMethod.class);
    putMethod(GssApiMethod.class);
    putMethod(NoAcceptableMethod.class);
    putMethod(UsernamePasswordMethod.class);
  }

  /**
   * A private constructor.
   */
  private SocksMethodRegistry() {}

  /**
   * Puts a {@link SocksMethod} class into the SOCKS method registry with an instance of
   * {@link SocksMethod}.
   * 
   * @param socksMethod The instance of {@link SocksMethod}.
   */
  public static void putMethod(SocksMethod socksMethod) {
    methodsMap.put((byte) socksMethod.getByte(), socksMethod.getClass());
  }

  /**
   * Puts a {@link SocksMethod} class into the SOCKS method registry.
   * 
   * @param methodClass {@link SocksMethod} class.
   */
  public static void putMethod(Class<? extends SocksMethod> methodClass) {
    int b = 0;
    try {
      b = methodClass.newInstance().getByte();
    } catch (InstantiationException e) {
      logger.error(e.getMessage(), e);
    } catch (IllegalAccessException e) {
      logger.error(e.getMessage(), e);
    }
    methodsMap.put((byte) b, methodClass);
  }

  /**
   * Returns a {@link SocksMethod} class which value is equal the given byte.
   * 
   * @param b value of {@link SocksMethod}.
   * @return A {@link SocksMethod} class which value is equal the given byte.
   */
  public static Class<? extends SocksMethod> getByByte(byte b) {
    return methodsMap.get(b);
  }
}
