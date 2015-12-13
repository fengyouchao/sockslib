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
import java.net.InetSocketAddress;

public class UDPTestClient {

  public static void main(String[] args) throws IOException {
    DatagramSocket clientSocket = new DatagramSocket();
    String message = "Hello, UDP server";
    DatagramPacket buffer =
        new DatagramPacket(message.getBytes(), message.length(), new InetSocketAddress("0.0.0.0",
            5050));
    clientSocket.send(buffer);
    clientSocket.close();
  }

}
