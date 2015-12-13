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

package sockslib.example;

import sockslib.client.Socks5;
import sockslib.client.Socks5DatagramSocket;
import sockslib.common.net.MonitorDatagramSocketWrapper;
import sockslib.common.net.NetworkMonitor;
import sockslib.utils.ResourceUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Socks5UDPAssociateClient {

  public static void main(String[] args) throws InterruptedException {

    DatagramSocket clientSocket = null;
    Socks5 proxy = new Socks5(new InetSocketAddress("localhost", 1080));

    try {
      NetworkMonitor networkMonitor = new NetworkMonitor();
      clientSocket = new Socks5DatagramSocket(proxy);
      clientSocket = new MonitorDatagramSocketWrapper(clientSocket, networkMonitor);
      String message = "I am client using SOCKS proxy";
      byte[] sendBuffer = message.getBytes();
      DatagramPacket packet =
          new DatagramPacket(sendBuffer, sendBuffer.length, new InetSocketAddress("localhost",
              5050));
      clientSocket.send(packet);


      //Received response message from UDP server.
      byte[] receiveBuf = new byte[100];
      DatagramPacket receivedPacket = new DatagramPacket(receiveBuf, receiveBuf.length);
      clientSocket.receive(receivedPacket);
      String receiveStr = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
      System.out.println("received:" + receiveStr);

      System.out.println("UDP client information:");
      System.out.println("Total Sent:     " + networkMonitor.getTotalSend() + " bytes");
      System.out.println("Total Received: " + networkMonitor.getTotalReceive() + " bytes");
      System.out.println("Total:          " + networkMonitor.getTotal() + " bytes");

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      ResourceUtil.close(clientSocket);
    }

  }

}
