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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import fucksocks.client.Socks5;
import fucksocks.client.Socks5DatagramSocket;

public class TestSocks5UDPAssociate {

  public static void main(String[] args) throws InterruptedException {

    DatagramSocket clientSocket = null;
    Socks5 proxy = new Socks5(new InetSocketAddress("localhost", 1080));

    try {
      clientSocket = new Socks5DatagramSocket(proxy);
      String message = "Are you UDP server?";
      byte[] buffer = message.getBytes();
      clientSocket.send(new DatagramPacket(buffer, buffer.length, new InetSocketAddress(
          "localhost", 5050)));
      byte[] recvBuf = new byte[100];
      DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
      clientSocket.receive(recvPacket);
      String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
      System.out.println("received:" + recvStr);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      ResourceUtil.close(clientSocket);
    }

  }

}
