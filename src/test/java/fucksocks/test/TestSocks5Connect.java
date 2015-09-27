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

import fucksocks.client.Socks5;
import fucksocks.client.SocksProxy;
import fucksocks.client.SocksSocket;
import fucksocks.common.net.MonitorSocketWrapper;
import fucksocks.common.net.NetworkMonitor;
import fucksocks.utils.ResourceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * <code>TestSocks5Connect</code> is a test class. It use SOCKS5's CONNECT command to query WHOIS
 * from a WHOIS server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 24, 2015 10:22:42 PM
 */
public class TestSocks5Connect {

  public static void main(String[] args) {

    long start = System.currentTimeMillis();
    InputStream inputStream = null;
    OutputStream outputStream = null;
    Socket socket = null;
    StringBuffer response = null;
    int length = 0;
    byte[] buffer = new byte[2048];

    try {
      SocksProxy proxy = new Socks5(new InetSocketAddress("localhost", 1080));
      socket = new SocksSocket(proxy, new InetSocketAddress("whois.internic.net", 43));
      //      socket = new Socket("whois.internic.net", 43);
      NetworkMonitor networkMonitor = new NetworkMonitor();
      socket = new MonitorSocketWrapper(socket, networkMonitor);
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      PrintWriter printWriter = new PrintWriter(outputStream);
      printWriter.print("domain google.com\r\n"); // query google.com WHOIS.
      printWriter.flush();
      System.out.println("Send success");

      response = new StringBuffer();
      while ((length = inputStream.read(buffer)) > 0) {
        response.append(new String(buffer, 0, length));
      }

      System.out.println(response.toString());

      System.out.println("Total Sent:     " + networkMonitor.getTotalSend() + " bytes");
      System.out.println("Total Received: " + networkMonitor.getTotalReceive() + " bytes");
      System.out.println("Total:          " + networkMonitor.getTotal() + " bytes");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      ResourceUtil.close(inputStream, outputStream, socket);
    }
    System.out.println("Total Run Time:" + (System.currentTimeMillis() - start) + "ms");
  }
}
