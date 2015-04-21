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

package fucksocks.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import fucksocks.common.Socks5DatagramPacketHandler;

/**
 * 
 * The class <code>UDPRelyServer</code> represents a UDP rely 
 * server.
 * 
 * @author Youchao Feng
 * @date Apr 22, 2015 12:54:50 AM
 * @version 1.0
 *
 */
public class UDPRelyServer implements Runnable{

	private Socks5DatagramPacketHandler datagramPacketHandler = new Socks5DatagramPacketHandler();

	private DatagramSocket server;

	private int bufferSize = 1024 * 1024 * 5;

	private Thread thread;

	private boolean running = true;
	
	private SocketAddress clientAddresss;
	
	public UDPRelyServer(SocketAddress clientAddresss){
		this.clientAddresss = clientAddresss;
	}

	public SocketAddress start() throws SocketException{
		@SuppressWarnings("resource")
		DatagramSocket server = new DatagramSocket();
		SocketAddress socketAddress = new InetSocketAddress(server.getLocalPort());
		thread = new Thread(this);
		thread.start();
		return socketAddress;
	}

	@Override
	public void run() {
		try {
			byte[] recvBuf = new byte[bufferSize];
			DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
			while (running) {

				server.receive(packet);
				if (isFromClient(packet)) {
					datagramPacketHandler.decapsulate(packet);
					server.send(packet);
				} else {
					packet = datagramPacketHandler.encapsulate(packet);
					server.send(packet);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DatagramSocket getServer() {
		return server;
	}

	public void setServer(DatagramSocket server) {
		this.server = server;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	protected boolean isFromClient(DatagramPacket packet) {
		return false;
	}



}
