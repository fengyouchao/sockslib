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
import sockslib.client.SocksProxy;
import sockslib.client.SocksSocket;
import sockslib.common.net.MonitorSocketWrapper;
import sockslib.common.net.NetworkMonitor;
import sockslib.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static sockslib.utils.ResourceUtil.close;

/**
 * <code>TestSocks5Connect</code> is a test class. It use SOCKS5's CONNECT command to query WHOIS
 * from a WHOIS server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 24, 2015 10:22:42 PM
 */
public class TestSocks5Connect {

  private static final Logger logger = LoggerFactory.getLogger(TestSocks5Connect.class);

  public static void main(String[] args) {

    Timer.open();
    InputStream inputStream = null;
    OutputStream outputStream = null;
    Socket socket = null;
    int length = 0;
    byte[] buffer = new byte[2048];

    try {
      SocksProxy proxy = new Socks5(new InetSocketAddress("localhost", 1080));
      socket = new SocksSocket(proxy, new InetSocketAddress("whois.internic.net", 43));
      NetworkMonitor networkMonitor = new NetworkMonitor();
      socket = MonitorSocketWrapper.wrap(socket, networkMonitor);
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      PrintWriter printWriter = new PrintWriter(outputStream);
      printWriter.print("domain google.com\r\n"); // query google.com WHOIS.
      printWriter.flush();
      logger.info("Waiting response from server....");
      while ((length = inputStream.read(buffer)) > 0) {
        System.out.print(new String(buffer, 0, length));
      }

      logger.info("Total Sent:     " + networkMonitor.getTotalSend() + " bytes");
      logger.info("Total Received: " + networkMonitor.getTotalReceive() + " bytes");
      logger.info("Total:          " + networkMonitor.getTotal() + " bytes");
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } finally {
      close(inputStream, outputStream, socket);
    }
  }
}
