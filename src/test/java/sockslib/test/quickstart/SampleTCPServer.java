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

import sockslib.utils.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A sample TCP server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 9:08 AM
 */
public final class SampleTCPServer implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(SampleTCPServer.class);
  private int port;
  private ServerSocket serverSocket;

  public void start(int port) {
    this.port = port;
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  public void shutdown() {
    if (serverSocket != null && serverSocket.isBound()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void run() {
    Socket socket = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      serverSocket = new ServerSocket(port);
      socket = serverSocket.accept();
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      int b = 0;
      while ((b = inputStream.read()) > -1) {
        outputStream.write(b);
        if (b == '\n') {
          break;
        }
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
    } finally {
      ResourceUtil.close(inputStream);
      ResourceUtil.close(outputStream);
      ResourceUtil.close(socket);
    }
  }
}
