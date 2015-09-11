/*
 * Copyright 2015-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fucksocks.test.server.manager;

import fucksocks.server.manager.MongoDBBasedUserManager;
import fucksocks.server.manager.User;
import fucksocks.utils.mongo.MongoDBUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Test {@link MongoDBBasedUserManager}<br>
 * Created by fengyouchao on 9/9/15.
 *
 * @author fengyouchao
 * @version 1.0
 */
public class TestMongoBasedUserManager {

  private MongoDBBasedUserManager userManager;
  private MongoDBUtil mongoDBUtil;

  @Before
  public void before(){
    userManager = MongoDBBasedUserManager.newDefaultUserManager();
    mongoDBUtil = userManager.getMongoDBUtil();
    mongoDBUtil.deleteAll(userManager.getUserCollectionName());
  }

  @After
  public void after(){
    mongoDBUtil.deleteAll(userManager.getUserCollectionName());
  }

  @Test
  public void testCreateAndFind(){
    final User user = new User("fucksocks", "123");
    userManager.create(user);
    User result = userManager.find(user.getUsername());
    Assert.assertEquals(user.getUsername(), result.getUsername());
  }

  @Test
  public void testFindAll(){
    final User user1 = new User("user1", "123");
    final User user2 = new User("user2", "456");
    userManager.create(user1);
    userManager.create(user2);
    List<User> users = userManager.findAll();
    Assert.assertEquals(2, users.size());
  }

  @Test
  public void testCheck(){
    final String username = "fucksocks";
    final String password = "123";
    final User user = new User(username, password);
    userManager.create(user);
    final User result1 = userManager.check(username, password);
    final User result2 = userManager.check(username, null);
    final User result3 = userManager.check(null, null);
    final User result4 = userManager.check(username + "123", password);
    final User result5 = userManager.check(username, password +"123");

    Assert.assertNotNull(result1);
    Assert.assertEquals(user.getUsername(), result1.getUsername());
    Assert.assertEquals(user.getPassword(), result1.getPassword());
    Assert.assertNull(result2);
    Assert.assertNull(result3);
    Assert.assertNull(result4);
    Assert.assertNull(result5);
  }

  @Test
  public void testDelete(){
    final User user = createTestUser();
    final User result1 = userManager.find(user.getUsername());
    userManager.delete(user.getUsername());
    final User result2 = userManager.find(user.getUsername());

    Assert.assertNotNull(result1);
    Assert.assertNull(result2);
  }

  @Test
  public void testUpdate(){
    final User user = createTestUser();
    User user2 = user.copy();
    final String newPassword = user.getPassword() + "new";
    user2.setPassword(newPassword);
    userManager.update(user2);
    final User result = userManager.find(user.getUsername());

    Assert.assertNotNull(result);
    Assert.assertEquals(result.getUsername(), user2.getUsername());
    Assert.assertEquals(result.getPassword(), user2.getPassword());
  }

  private User createTestUser(){
    final User user = new User("fucksocks", "123");
    userManager.create(user);
    return user;
  }

}
