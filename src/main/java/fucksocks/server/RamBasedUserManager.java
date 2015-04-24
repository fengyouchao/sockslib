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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class <code>RamBasedUserManager</code> represents a user manager that manage users in RAM.
 *
 * @author Youchao Feng
 * @date Apr 16, 2015 3:00:27 PM
 * @version 1.0
 *
 */
public class RamBasedUserManager implements UserManager {

  private Map<String, User> users = new HashMap<>();

  @Override
  public void addUser(String username, String password) {
    users.put(username, new User(username, password));
  }

  @Override
  public User findUser(String username, String password) {
    User user = users.get(username);
    if (user != null && user.getPassword().equals(password)) {
      return user;
    }
    return null;
  }

  @Override
  public User deleteUser(String username) {
    return users.remove(username);
  }

  @Override
  public List<User> findAll() {
    // Not implemented.
    return null;
  }

}
