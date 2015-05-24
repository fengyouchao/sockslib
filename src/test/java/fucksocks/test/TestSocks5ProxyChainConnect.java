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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import fucksocks.client.Socks5;
import fucksocks.client.SocksProxy;
import fucksocks.client.SocksSocket;
import fucksocks.common.UsernamePasswordCredentials;

/**
 * 
 * <code>TestSocks5Connect</code> is a test class. It use SOCKS5's CONNECT command to query WHOIS
 * from a WHOIS server.
 * 
 * @author Youchao Feng
 * @date Mar 24, 2015 10:22:42 PM
 * @version 1.0
 *
 */
public class TestSocks5ProxyChainConnect {

  public static void main(String[] args) {

    Socket socket = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    StringBuffer response = null;
    byte[] buffer = new byte[2048];
    int length = 0;

    SocksProxy proxy1 = new Socks5(new InetSocketAddress("localhost", 1080));
    proxy1.setCredentials(new UsernamePasswordCredentials("socks", "1234"));
    SocksProxy proxy2 = new Socks5(new InetSocketAddress("localhost", 1081));
    proxy2.setCredentials(new UsernamePasswordCredentials("socks", "1234"));

    SocksProxy proxy3 = new Socks5(new InetSocketAddress("localhost", 1082));
    proxy3.setCredentials(new UsernamePasswordCredentials("socks", "1234"));

    proxy1.setChainProxy(proxy2.setChainProxy(proxy3));
    
    System.out.println("USE proxy:"+proxy1);

    try {

      socket = new SocksSocket(proxy1);
      socket.connect(new InetSocketAddress("whois.internic.net", 43));

      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      PrintWriter printWriter = new PrintWriter(outputStream);
      printWriter.print("domain google.com\r\n");
      printWriter.flush();

      length = 0;
      response = new StringBuffer();
      while ((length = inputStream.read(buffer)) > 0) {
        response.append(new String(buffer, 0, length));
      }
      System.out.println(response.toString());

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      ResourceUtil.close(inputStream, outputStream, socket);
    }

  }

}
