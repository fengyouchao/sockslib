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

package fucksocks.server.msg;

import fucksocks.common.methods.SocksMethod;


/**
 * 
 * The class <code>MethodSeleteionResponseMessage</code> represents
 * 
 * @author Youchao Feng
 * @date Apr 6, 2015 11:10:05 AM
 * @version 1.0
 *
 */
public class MethodSeleteionResponseMessage implements WritableMessage {

  private int version;

  private int method;


  public MethodSeleteionResponseMessage() {

  }

  public MethodSeleteionResponseMessage(int version, int method) {
    this.version = version;
    this.method = method;
  }

  public MethodSeleteionResponseMessage(int version, SocksMethod socksMethod) {
    this(version, socksMethod.getByte());
  }

  @Override
  public byte[] getBytes() {
    byte[] bytes = new byte[2];
    bytes[0] = (byte) version;
    bytes[1] = (byte) method;
    return bytes;
  }

  @Override
  public int getLength() {
    return getBytes().length;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getMethod() {
    return method;
  }

  public void setMethod(int method) {
    this.method = method;
  }

}
