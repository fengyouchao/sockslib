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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import fucksocks.server.msg.IncomeMessage;
import fucksocks.server.msg.Message;

/**
 * 
 * The class <code>Session</code> is a encapsulation of socket.
 * 
 * @author Youchao Feng
 * @date Apr 5, 2015 10:21:28 AM
 * @version 1.0
 *
 */
public interface Session {
	
	/**
	 * Gets socket.
	 * 
	 * @return socket that connected remote host.
	 */
	public Socket getSocket();
	
	/**
	 * Writes bytes in output stream.
	 * 
	 * @param bytes bytes
	 */
	public void write(byte[] bytes);
	
	/**
	 * Writes bytes in output stream.
	 * 
	 * @param bytes		bytes
	 * @param offset	offset
	 * @param length	bytes length.
	 */
	public void write(byte[] bytes, int offset, int length);
	
	/**
	 * Writes <code>Message</code> in output stream.
	 * 
	 * @param message {@link Message} instance.
	 */
	public void write(Message message);
	
	/**
	 * Reads
	 * @param byetes
	 * @return
	 */
	public int read(byte[] byetes);
	
	public int read(IncomeMessage message);
	
	public long getId();
	
	public void close();
	
	public InputStream getInputStream();

	public OutputStream getOutputStream();
	
	public Map<Long, Session> getManagedSessions();

}
