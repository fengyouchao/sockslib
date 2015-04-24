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

import java.io.IOException;

import fucksocks.server.msg.ServerReply;

/**
 * The class <code>SocksException</code> represents an exception about SOCKS protocol.
 * 
 * @author Youchao Feng
 * @date Mar 18, 2015 9:24:59 AM
 * @version 1.0
 */
public class SocksException extends IOException {

  private static final long serialVersionUID = 1L;

  private static final String NO_ACCEPTABLE_METHODS = "NO ACCEPTABLE METHODS";

  private ServerReply serverReply;

  private static final String serverReplyMessage[] = {"General SOCKS server failure",
      "Connection not allowed by ruleset", "Network unreachable", "Host unreachable",
      "Connection refused", "TTL expired", "Command not supported", "Address type not supported"};

  public SocksException(String msg) {
    super(msg);
  }

  public SocksException(int replyCode) {

  }

  public static final SocksException noAcceptableMethods() {
    return new SocksException(NO_ACCEPTABLE_METHODS);
  }

  public static final SocksException protocolNotSupported() {
    return new SocksException("Protocol not suppoted");
  }

  public static final SocksException serverReplyException(ServerReply reply) {
    SocksException ex = serverReplyException(reply.getValue());
    ex.setServerReply(reply);
    return ex;
  }

  public static final SocksException serverReplyException(byte reply) {
    int code = reply;
    code = code & 0xff;
    if (code < 0 || code > 0x08) {
      return new SocksException("Unknown reply");
    }
    code = code - 1;
    return new SocksException(serverReplyMessage[code]);
  }

  public ServerReply getServerReply() {
    return serverReply;
  }

  public void setServerReply(ServerReply serverReply) {
    this.serverReply = serverReply;
  }

}
