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

package fucksocks.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import fucksocks.common.SocksException;
import fucksocks.server.msg.WritableMessage;
import fucksocks.server.msg.ReadableMessage;

/**
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
  public void write(byte[] bytes) throws SocksException, IOException;

  /**
   * Writes bytes in output stream.
   * 
   * @param bytes bytes
   * @param offset offset
   * @param length bytes length.
   */
  public void write(byte[] bytes, int offset, int length) throws SocksException, IOException;

  /**
   * Writes <code>Message</code> in output stream.
   * 
   * @param message {@link WritableMessage} instance.
   */
  public void write(WritableMessage message) throws SocksException, IOException;

  /**
   * Read a buffer.
   * 
   * @param byetes Buffer which read in.
   * @return Read length
   */
  public int read(byte[] byetes) throws SocksException, IOException;

  /**
   * Reads a message.
   * 
   * @param message a readable message.
   * @return Read bytes size.
   */
  public int read(ReadableMessage message) throws SocksException, IOException;

  /**
   * Gets session ID.
   * 
   * @return session ID.
   */
  public long getId();


  /**
   * Closes connection.
   */
  public void close();

  /**
   * Gets input stream.
   * 
   * @return Input stream.
   */
  public InputStream getInputStream();

  /**
   * Gets output stream.
   * 
   * @return Output stream.
   */
  public OutputStream getOutputStream();


  /**
   * Gets all sessions that be managed.
   * 
   * @return All sessions.
   */
  public Map<Long, Session> getManagedSessions();

  /**
   * Get remote host's IP address and port.
   * 
   * @return Remote host's IP address and port.
   */
  public SocketAddress getRemoteAddress();

  public void setAttribute(Object key, Object value);

  public Object getAttribute(Object key);

  public Map<Object, Object> getAttributes();

  public void clearAllAttributes();

  public boolean isClose();

  public boolean isConnected();

}
