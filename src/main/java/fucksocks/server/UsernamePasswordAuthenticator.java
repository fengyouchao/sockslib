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

import fucksocks.common.Authentication;
import fucksocks.common.AuthenticationException;
import fucksocks.common.UsernamePasswordAuthentication;
import fucksocks.common.methods.UsernamePasswordMethod;

/**
 * The class <code>UsernamePasswordAuthenticator</code> represents a username password
 * authenticator. It will be used by {@link UsernamePasswordMethod}.
 *
 * @author Youchao Feng
 * @date Apr 16, 2015 11:30:46 AM
 * @version 1.0
 *
 */
public class UsernamePasswordAuthenticator implements Authenticator {

  /**
   * {@link RamBasedUserManager} is default.
   */
  private UserManager userManager = new RamBasedUserManager();

  public static final String USER_KEY = "USER";

  @Override
  public void doAuthenticate(Authentication authentication, Session session)
      throws AuthenticationException {
    if (authentication instanceof UsernamePasswordAuthentication) {
      UsernamePasswordAuthentication auth = (UsernamePasswordAuthentication) authentication;
      String username = auth.getUsername();
      String password = auth.getPassword();
      User user = userManager.findUser(username, password);
      if (user == null) {
        throw new AuthenticationException("Not valid user");
      }
      session.setAttribute(USER_KEY, user);

    } else {
      throw new AuthenticationException("Only support Username/Password Authentication");
    }

  }

  public UserManager getUserManager() {
    return userManager;
  }

  public void setUserManager(UserManager userManager) {
    this.userManager = userManager;
  }

  public void addUser(String username, String password) {
    userManager.addUser(username, password);
  }

  public User deleteUser(String username) {
    return userManager.deleteUser(username);
  }

}
