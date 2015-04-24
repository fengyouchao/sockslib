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

package fucksocks.server.msg;

import java.io.IOException;
import java.io.InputStream;

import fucksocks.common.SocksException;
import fucksocks.common.UsernamePasswordAuthentication;


/**
 * The class <code>UsernamePasswordMessage</code> represents a USERNAME/PASSWROD authentication
 * message.
 *
 * @author Youchao Feng
 * @date Apr 16, 2015 11:41:28 AM
 * @version 1.0
 *
 */
public class UsernamePasswordMessage implements ReadableMessage, WritableMessage {

  private UsernamePasswordAuthentication usernamePasswordAutencation;

  private int version = 0x01;

  private int usernameLength;

  private int passwordLength;

  private String username;

  private String password;

  public UsernamePasswordMessage() {

  }

  public UsernamePasswordMessage(String username, String password) {
    this.username = username;
    this.password = password;
    usernameLength = username.getBytes().length;
    passwordLength = password.getBytes().length;
  }

  @Override
  public byte[] getBytes() {

    final int SIZE = 3 + usernameLength + passwordLength;

    byte[] bytes = new byte[SIZE];

    bytes[0] = (byte) version;
    bytes[1] = (byte) usernameLength;
    for (int i = 0; i < usernameLength; i++) {
      bytes[2 + i] = username.getBytes()[i];
    }

    bytes[2 + usernameLength] = (byte) passwordLength;

    for (int i = 0; i < passwordLength; i++) {
      bytes[3 + usernameLength + i] = password.getBytes()[i];
    }

    return bytes;
  }

  @Override
  public int getLength() {
    return getBytes().length;
  }

  @Override
  public void read(InputStream inputStream) throws SocksException, IOException {
    version = inputStream.read();
    usernameLength = inputStream.read();
    byte[] buffer = new byte[usernameLength];
    int size = inputStream.read(buffer);

    if (size != usernameLength) {
      throw new SocksException("Protocol error");
    }
    username = new String(buffer);
    passwordLength = inputStream.read();
    buffer = new byte[passwordLength];
    size = inputStream.read(buffer);
    if (size != passwordLength) {
      throw new SocksException("Protocol error");
    }

    password = new String(buffer);

    usernamePasswordAutencation = new UsernamePasswordAuthentication(username, password);
  }



  public int getVersion() {
    return version;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UsernamePasswordAuthentication getUsernamePasswordAutentication() {
    return usernamePasswordAutencation;
  }

}
