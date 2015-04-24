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

import java.net.InetAddress;
import java.net.UnknownHostException;

import fucksocks.common.AddressType;
import fucksocks.common.NotImplementException;
import fucksocks.utils.SocksUtil;

/**
 * The class <code>CommandResponseMessage</code> represents a command response message.
 * 
 * @author Youchao Feng
 * @date Apr 6, 2015 11:10:25 AM
 * @version 1.0
 *
 */
public class CommandResponseMessage implements WritableMessage {

  private int version = 5;

  private int reserved = 0x00;

  private int addressType = AddressType.IPV4;

  private InetAddress bindAddress;

  private int bindPort;

  private ServerReply reply;

  public CommandResponseMessage(ServerReply reply) {
    byte[] defaultAddress = {0, 0, 0, 0};
    this.reply = reply;
    try {
      bindAddress = InetAddress.getByAddress(defaultAddress);
      addressType = 0x01;
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  public CommandResponseMessage(int version, ServerReply reply, InetAddress bindAddress,
      int bindPort) {
    this.version = version;
    this.reply = reply;
    this.bindAddress = bindAddress;
    this.bindPort = bindPort;
    if (bindAddress.getAddress().length == 4) {
      addressType = 0x01;
    } else {
      addressType = 0x04;
    }
  }

  @Override
  public byte[] getBytes() {
    byte[] bytes = null;

    switch (addressType) {
      case AddressType.IPV4:
        bytes = new byte[10];
        for (int i = 0; i < bindAddress.getAddress().length; i++) {
          bytes[i + 4] = bindAddress.getAddress()[i];
        }
        bytes[8] = SocksUtil.getFisrtByteFromPort(bindPort);
        bytes[9] = SocksUtil.getSecondByteFromPort(bindPort);
        break;
      case AddressType.IPV6:
        bytes = new byte[22];
        for (int i = 0; i < bindAddress.getAddress().length; i++) {
          bytes[i + 4] = bindAddress.getAddress()[i];
        }
        bytes[20] = SocksUtil.getFisrtByteFromPort(bindPort);
        bytes[21] = SocksUtil.getSecondByteFromPort(bindPort);
        break;
      case AddressType.DOMAINNAME:
        throw new NotImplementException();
      default:
        break;
    }

    bytes[0] = (byte) version;
    bytes[1] = reply.getValue();
    bytes[2] = (byte) reserved;
    bytes[3] = (byte) addressType;

    return bytes;
  }

  @Override
  public int getLength() {
    return getBytes().length;
  }

}
