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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import fucksocks.client.Socks5;
import fucksocks.client.SocksProxy;
import fucksocks.client.SocksSocket;
import fucksocks.common.methods.NoAuthencationRequiredMethod;
import fucksocks.common.methods.SocksMethod;
import fucksocks.utils.SocksClientSSLUtil;

/**
 * SSL Client
 * 
 * @author Leo
 */
public class SSLSocketClient {

  public static void main(String[] args) throws Exception {

    SocksClientSSLUtil sslUtil = SocksClientSSLUtil.loadClassPath("client-ssl.properties");

    SocksProxy proxy = new Socks5(new InetSocketAddress("localhost", 1080));
    List<SocksMethod> methods = new ArrayList<SocksMethod>();
    methods.add(new NoAuthencationRequiredMethod());
    proxy.setAcceptableMethods(methods);
    SocksSocket socket = sslUtil.create(proxy);

    socket.connect("whois.internic.net", 43);
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
    byte[] resultbyte = new byte[bytelist.size()];
    for (int i = 0; i < resultbyte.length; i++) {
      resultbyte[i] = bytelist.get(i);
    }
    String string = new String(resultbyte);
    System.out.println(string);

    inputStream.close();
    outputStream.close();
    socket.close();
  }

}
