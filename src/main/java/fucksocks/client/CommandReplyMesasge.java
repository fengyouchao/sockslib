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

package fucksocks.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import fucksocks.utils.SocksUtil;

/**
 * The class <code>RequestCmdReplyMesasge</code> represents the reply message from SOCKS server when
 * client sends a command request.
 * 
 * @author Youchao Feng
 * @date Mar 23, 2015 5:55:06 PM
 * @version 1.0
 */
public class CommandReplyMesasge implements SocksMessage {

  /**
   * The bytes that received from SOCKS server.
   */
  private byte[] replyBytes;

  /**
   * Constructs CommandReplyMesasge instance by bytes that received from SOCKS server.
   * 
   * @param replyBytes The bytes that received from SOCKS server.
   */
  public CommandReplyMesasge(byte[] replyBytes) {
    this.replyBytes = replyBytes;
  }


  public boolean isSuccess() {
    if (replyBytes.length < 10) {
      return false;
    }
    return replyBytes[1] == 0;
  }

  public InetAddress getIp() throws UnknownHostException {
    byte[] addressBytes = null;

    if (replyBytes[3] == Socks5.ATYPE_IPV4) {
      addressBytes = new byte[4];
    }

    else if (replyBytes[3] == Socks5.ATYPE_IPV6) {
      addressBytes = new byte[16];
    }

    System.arraycopy(replyBytes, 4, addressBytes, 0, addressBytes.length);
    return InetAddress.getByAddress(addressBytes);
  }

  public int getPort() {

    return SocksUtil.bytesToPort(replyBytes[replyBytes.length - 2],
        replyBytes[replyBytes.length - 1]);
  }

  public byte[] getReplyBytes() {
    return replyBytes;
  }

  public void setReplyBytes(byte[] replyBytes) {
    this.replyBytes = replyBytes;
  }
  
  public SocketAddress getSocketAddress(){
    try {
      return new InetSocketAddress(getIp(), getPort());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }

}
