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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * The class <code>UDPTimeServer</code> is a UDP server used to test SOCKS protocol.
 * <p>
 * It's a very simple UDP server, it listen at 5050 port, and outputs the message that sent by
 * client, then sends "Yes, I am UDP server" to client.
 * </p>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 24, 2015 2:15:24 PM
 */
public class UDPTestServer {

  public static void main(String[] args) throws IOException {
    int port = 5050;
    if (args.length == 1) {
      port = Integer.parseInt(args[0]);
    }

    DatagramSocket server = new DatagramSocket(port);
    System.out.println("Listening port at:" + server.getLocalPort());
    byte[] receiveBuffer = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
    final String responseMsg = "Yes, I am UDP server";

    while (true) {
      server.receive(receivePacket);
      String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
      System.out.println("received message:" + msg);
      if (msg.equals("quit")) {
        break;
      }

      System.out.println("Response:" + responseMsg);
      byte[] data = responseMsg.getBytes();
      DatagramPacket packet =
          new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort
              ());
      server.send(packet);
    }
    server.close();
  }
}
