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

/**
 * The class <code>SocksMethodRegistry</code>
 * 
 * @author Youchao Feng
 * @date Mar 17, 2015 11:12:06 AM
 * @version 1.0
 * 
 */
public class SocksMethodRegistry {

  private static final HashMap<Byte, Class<? extends SocksMethod>> methodsMap = new HashMap<>();

  static {
    putMethod(NoAuthencationRequiredMethod.class);
    putMethod(GssApiMethod.class);
    putMethod(NoAcceptableMethod.class);
    putMethod(UsernamePasswordMethod.class);
  }

  public static void putMethod(SocksMethod socksMethod) {
    methodsMap.put((byte) socksMethod.getByte(), socksMethod.getClass());
  }

  public static void putMethod(Class<? extends SocksMethod> methodClass) {
    int b = 0;
    try {
      b = methodClass.newInstance().getByte();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    methodsMap.put((byte) b, methodClass);
  }

  public static Class<? extends SocksMethod> getByByte(byte b) {
    return methodsMap.get(b);
  }
}
