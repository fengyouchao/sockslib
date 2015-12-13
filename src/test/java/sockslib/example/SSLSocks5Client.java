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

import sockslib.client.SSLSocks5;
import sockslib.client.SocksProxy;
import sockslib.client.SocksSocket;
import sockslib.common.SSLConfiguration;
import sockslib.common.net.MonitorSocketWrapper;
import sockslib.common.net.NetworkMonitor;
import sockslib.utils.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static sockslib.utils.ResourceUtil.close;

/**
 * The class <code>SSLSocks5Client</code> is a client to connect a SSL based SOCKS5 proxy
 * server.
 *
 * @author Youchao Feng
 * @version 1.0
 * @since 1.0
 */
public class SSLSocks5Client {

  private static final Logger logger = LoggerFactory.getLogger(SSLSocks5Client.class);

  public static void main(String[] args) {

    Timer.open();
    InputStream inputStream = null;
    OutputStream outputStream = null;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Socket socket = null;
    NetworkMonitor networkMonitor = new NetworkMonitor();
    int length = 0;
    byte[] buffer = new byte[2048];

    try {
      SSLConfiguration configuration = SSLConfiguration.loadClassPath("client-ssl.properties");
      SocksProxy proxy = new SSLSocks5(new InetSocketAddress("localhost", 1081), configuration);
      socket = new SocksSocket(proxy, new InetSocketAddress("whois.internic.net", 43));
      socket = MonitorSocketWrapper.wrap(socket, networkMonitor);
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      PrintWriter printWriter = new PrintWriter(outputStream);
      printWriter.print("domain google.com\r\n"); // query google.com WHOIS.
      printWriter.flush();
      logger.info("Waiting response from server...");
      while ((length = inputStream.read(buffer)) > 0) {
        byteArrayOutputStream.write(buffer, 0, length);
      }
      //      logger.info("Server response:\n{}",new String(byteArrayOutputStream.toByteArray(),
      // 9, byteArrayOutputStream.toByteArray().length-9));
      logger.info("Server response:\n{}", new String(byteArrayOutputStream.toByteArray()));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      close(inputStream, outputStream, socket);
      close(byteArrayOutputStream);
    }
  }

}
