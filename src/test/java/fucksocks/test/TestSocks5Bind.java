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

package fucksocks.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import fucksocks.client.Socks5;
import fucksocks.client.SocksServerSocket;
import fucksocks.common.SocksException;

public class TestSocks5Bind {

  public static final void main(String[] args) throws SocksException, IOException,
      InterruptedException {

    Socks5 proxy = new Socks5(new InetSocketAddress(InetAddress.getByName("localhost"), 1080));
    InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
    @SuppressWarnings("resource")
    SocksServerSocket serverSocket = new SocksServerSocket(proxy, inetAddress, 8080);
    serverSocket.getBindAddress();
    Socket socket = serverSocket.accept();

    while (true) {
      byte[] bytes = new byte[1024];
      int size = socket.getInputStream().read(bytes);
      byte[] message = Arrays.copyOf(bytes, size);
      System.out.println("received：" + new String(message));
      String response = "you have send " + message.length + " characters\n";
      socket.getOutputStream().write(response.getBytes());
      socket.getOutputStream().flush();
    }
  }

}
