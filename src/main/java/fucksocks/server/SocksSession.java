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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import fucksocks.server.msg.Message;
import fucksocks.server.msg.ReadableMessage;


/**
 * The class <code>SocksSession</code> represents
 * 
 * @author Youchao Feng
 * @date Apr 5, 2015 10:21:36 AM
 * @version 1.0
 *
 */
public class SocksSession implements Session{

	private Socket socket;

	private long id;

	private InputStream inputStream;

	private OutputStream outputStream;
	
	private Map<Long, Session> sessions;
	
	private SocketAddress remoteSocketAddress;

	public SocksSession() {
	}

	public SocksSession(long id, Socket socket, Map<Long, Session> sessions){
		if(!socket.isConnected()){
			throw new IllegalArgumentException("Socket should be a connected socket");
		}
		this.id = id;
		this.socket = socket;
		this.sessions = sessions;
		try {
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		remoteSocketAddress = socket.getRemoteSocketAddress();

	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	@Override
	public void write(byte[] bytes) {
		try {
			outputStream.write(bytes);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(Message message) {
		write(message.getBytes());
	}

	@Override
	public int read(byte[] byetes) {
		try {
			return inputStream.read(byetes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int read(ReadableMessage message) {
		message.read(inputStream);
		return message.getBytes().length;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void close() {
		try {
			if(inputStream != null){
				inputStream.close();
			}
			if(outputStream != null){
				outputStream.close();
			}
			if (socket !=null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			sessions.remove(id);
		}
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void write(byte[] bytes, int offset, int length) {
		try {
			outputStream.write(bytes, offset, length);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<Long, Session> getManagedSessions() {
		return sessions;
	}
	
	@Override
	public SocketAddress getRemoteAddress(){
		return remoteSocketAddress;
	}

}
