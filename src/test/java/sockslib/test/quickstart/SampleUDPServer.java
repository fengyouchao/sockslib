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

package sockslib.test.quickstart;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * A sample UDP server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 11:16 AM
 */
public final class SampleUDPServer implements Runnable {

  private int port;
  private DatagramSocket socket;

  public void start(int port) {
    this.port = port;
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  public void shutdown() {
    if (socket != null && socket.isBound()) {
      socket.close();
    }
  }

  @Override
  public void run() {
    try {
      socket = new DatagramSocket(port);
      byte[] receiveBuffer = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
      socket.receive(receivePacket);
      byte[] data = receivePacket.getData();
      DatagramPacket packet =
          new DatagramPacket(data, receivePacket.getLength(), receivePacket.getAddress(),
              receivePacket.getPort());
      socket.send(packet);
      socket.close();
      socket = null;
    } catch (java.io.IOException e) {
      e.printStackTrace();
    }
  }

  public int getPort() {
    return port;
  }
}
