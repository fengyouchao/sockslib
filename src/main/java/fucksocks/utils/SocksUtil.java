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

package fucksocks.utils;

/**
 * <code>SocksUtil</code> is a tool class.
 * 
 * @author Youchao Feng
 * @date Mar 24, 2015 4:16:22 PM
 * @version 1.0
 */
public class SocksUtil {

  public static byte[] portTobytes(int port) {
    byte[] array = new byte[2];
    array[0] = (byte) ((port & 0xff00) >> 8);
    array[1] = (byte) (port & 0xff);
    return array;
  }

  public static byte getFisrtByteFromPort(int port) {
    return (byte) ((port & 0xff00) >> 8);
  }

  public static byte getSecondByteFromPort(int port) {
    return (byte) (port & 0xff);
  }

  public static int bytesToPort(byte[] bytes) {
    if (bytes.length != 2) {
      throw new IllegalArgumentException("byte array size must be 2");
    }
    return bytesToPort(bytes[0], bytes[1]);
  }

  public static int bytesToPort(byte b1, byte b2) {
    return (UnsignedByte.toInt(b1) << 8) | UnsignedByte.toInt(b2);
  }

}
