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
import java.net.UnknownHostException;
import java.util.ArrayList;

import fucksocks.client.Socks5;
import fucksocks.client.SocksProxy;
import fucksocks.client.SocksSocket;
import fucksocks.common.SocksException;
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

    SocksProxy proxy1 = new Socks5(new InetSocketAddress("localhost", 1080));
    proxy1.setCredentials(new UsernamePasswordCredentials("socks", "1234"));
    SocksProxy proxy2 = new Socks5(new InetSocketAddress("localhost", 1081));
    proxy2.setCredentials(new UsernamePasswordCredentials("socks", "1234"));

    SocksProxy proxy3 = new Socks5(new InetSocketAddress("localhost", 1082));
    proxy3.setCredentials(new UsernamePasswordCredentials("socks", "1234"));

    proxy1.setChainProxy(proxy2.setChainProxy(proxy3.setChainProxy(proxy1.copy().setChainProxy(
        proxy2.copy()))));
    try {

      @SuppressWarnings("resource")
      Socket socket = new SocksSocket(proxy1);
      socket.connect(new InetSocketAddress("whois.internic.net", 43));

      InputStream inputStream = socket.getInputStream();
      OutputStream outputStream = socket.getOutputStream();
      PrintWriter printWriter = new PrintWriter(outputStream);
      printWriter.print("domain google.com\r\n");
      printWriter.flush();

      byte[] whoisrecords = new byte[2048];
      java.util.List<Byte> bytelist = new ArrayList<>(1024 * 6);
      int size = 0;
      while ((size = inputStream.read(whoisrecords)) > 0) {
        for (int i = 0; i < size; i++) {
          bytelist.add(whoisrecords[i]);
        }
      }
      System.out.println("size:" + bytelist.size());
      byte[] resultbyte = new byte[bytelist.size()];
      for (int i = 0; i < resultbyte.length; i++) {
        resultbyte[i] = bytelist.get(i);
      }
      String string = new String(resultbyte);
      System.out.println(string);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (SocksException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
