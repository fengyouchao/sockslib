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
 * The class <code>KeyStoreInfo</code> represents a key store information.
 *
 * @author Youchao Feng
 * @version 1.0
 * @since 1.0
 */
public class KeyStoreInfo {

  private String keyStorePath;
  private String password;
  private String type;

  public KeyStoreInfo() {
  }

  public KeyStoreInfo(String keyStorePath, String password, String type) {
    this.keyStorePath = keyStorePath;
    this.password = password;
  }

  public KeyStoreInfo(String keyStorePath, String password) {
    this(keyStorePath, password, "JKS");
  }

  public String getKeyStorePath() {
    return keyStorePath;
  }

  public KeyStoreInfo setKeyStorePath(String keyStorePath) {
    this.keyStorePath = keyStorePath;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public KeyStoreInfo setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "[KEY STORE] PATH:" + keyStorePath + " PASSWORD:" + password + " TYPE:" + type;
  }

}
