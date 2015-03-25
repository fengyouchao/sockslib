/* 
 * Copyright 2015-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fucksocks.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.common.SocksException;

/**
 * The class <code>SocksServerSocket</code> is server socket 
 * that can bind a port at SOCKS server and accept connections 
 * by SOCKS4 or SOCKS5
 * protocol.
 * 
 * @author Youchao Feng
 * @date  Mar 25, 2015 11:40:36 AM 
 * @version 1.0
 */
public class SocksServerSocket extends ServerSocket{
	
	protected static final Logger logger = LoggerFactory.getLogger(SocksServerSocket.class);
	
	private SocksProxy proxy;
	
	private int port;
	
	private InetAddress bindAddress;
	
	public SocksServerSocket(SocksProxy proxy, int port, InetAddress bindAddress)
			throws SocksException, IOException {
		this.proxy = proxy;
		this.port = port;
		this.bindAddress = bindAddress;
	}
	
	public SocksServerSocket(SocksProxy proxy, int port)
			throws SocksException, IOException {
		this(proxy, port, InetAddress.getLocalHost());
	}

	@Override
	public Socket accept() throws SocksException, IOException {
		proxy.buildConnection();
		CommandReplyMesasge  replyMesasge = proxy.requestBind(bindAddress, port);
		bindAddress = replyMesasge.getIp();
		port = replyMesasge.getPort();
		logger.info("Bind at {}:{}", bindAddress, port);
		
		InputStream inputStream = proxy.getInputStream();
		byte[] bytes = new byte[2048];
		int size = inputStream.read(bytes);
		System.out.println(new String(bytes, 0, size));
		return proxy.getProxySocket();
	}
}
