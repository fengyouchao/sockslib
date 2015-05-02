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

package fucksocks.common;

/**
 * The class <code>UsernamePasswordAuthencation</code> represents an USERNAME/PASSWORD based
 * authentication. This authentication is only supported by SOCKS5 protocol.
 * 
 * @author Youchao Feng
 * @date Mar 18, 2015 9:58:58 AM
 * @version 1.0
 */
public class UsernamePasswordAuthentication implements Authentication {

  /**
   * Username.
   */
  private String username;

  /**
   * Password.
   */
  private String password;

  /**
   * Constructs an instance of {@link UsernamePasswordAuthentication}.
   */
  public UsernamePasswordAuthentication() {
    this(null, null);
  }

  /**
   * Constructs an instance of {@link UsernamePasswordAuthentication} with a username and a password.
   * 
   * @param username Username.
   * @param password Password.
   */
  public UsernamePasswordAuthentication(String username, String password) {
    this.username = username;
    this.password = password;
  }


  /**
   * Returns username.
   * 
   * @return username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets username.
   * 
   * @param username Username.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns password.
   * 
   * @return Password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets password.
   * 
   * @param password Password.
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
