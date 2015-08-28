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

package fucksocks.server.manager;

import java.util.List;

/**
 * The class <code>UserManager</code> represents a manager that can manage users.
 *
 * @author Youchao Feng
 * @date Apr 16, 2015 11:30:18 AM
 * @version 1.0
 *
 */
public interface UserManager {

  void create(User user);

  /**
   * Adds a user to the {@link UserManager}.
   *  @param username Username.
   * @param password Password.
   */
  Void addUser(String username, String password);

  /**
   * finds a user by username and password.
   * 
   * @param username Username.
   * @param password Password.
   * @return User.
   */
  User findUser(String username, String password);

  /**
   * Deletes a user from {@link UserManager} by username.
   * 
   * @param username Username.
   * @return Deleted user.
   */
  User deleteUser(String username);

  /**
   * Find all users.
   * 
   * @return All uusers.
   */
  List<User> findAll();

}
