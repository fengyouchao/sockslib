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

public enum ServerReply {

  SUCCESSED(0x00),
  GENERAL_SOCKS_SERVER_FAILURE(0x01),
  CONNECTION_NOT_ALLOWED_BY_RELESET(0x02),
  NETWORK_UNREACHABLE(0x03),
  HOST_UNREACHABLE(0x04),
  CONNECTION_REFUSED(0x05),
  TTL_EXPIRED(0x06),
  COMMAND_NOT_SUPPORTED(0x07),
  ADDRESS_TYPE_NOT_SUPPORTED(0x08);

  private byte value;

  private ServerReply(int value) {
    this.value = (byte) value;
  }

  public byte getValue() {
    return value;
  }

}
