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

package sockslib.test.client;

import socklib.test.Ports;
import sockslib.client.SocksProxy;
import sockslib.client.SocksSocket;
import sockslib.test.quickstart.SampleTCPServer;
import sockslib.utils.ResourceUtil;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 4:39 PM
 */
public class SocksTester {

  public static void checkConnect(SocksProxy proxy) throws IOException {
    SampleTCPServer server = new SampleTCPServer();
    int REMOTE_SERVER_PORT = Ports.unused();
    server.start(REMOTE_SERVER_PORT);
    Socket socket = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    ByteArrayOutputStream cache = new ByteArrayOutputStream();
    String sendMessage = "Hello fucksocks!\n";
    try {
      socket = new SocksSocket(proxy, Ports.localSocketAddress(REMOTE_SERVER_PORT));
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      outputStream.write(sendMessage.getBytes());
      outputStream.flush();
      byte[] buffer = new byte[1024 * 5];
      int length = 0;
      while ((length = inputStream.read(buffer)) > 0) {
        cache.write(buffer, 0, length);
      }
    } finally {
      ResourceUtil.close(inputStream);
      ResourceUtil.close(outputStream);
      ResourceUtil.close(socket);
      server.shutdown();
    }
    byte[] data = cache.toByteArray();
    Assert.assertEquals(sendMessage, new String(data));
  }
}
