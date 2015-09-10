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

import fucksocks.server.manager.JdbcBasedUserManager;
import fucksocks.server.manager.JdbcConfiguration;
import fucksocks.server.manager.User;
import fucksocks.utils.jdbc.JdbcTemplate;
import junit.framework.Assert;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link JdbcBasedUserManager}
 * <br>Created by fengyouchao on 9/9/15.
 */
public class TestJdbcBasedUserManager {

  private JdbcTemplate jdbcTemplate;
  private JdbcBasedUserManager userManager;
  private static final String USERNAME = "fucksocks";
  private static final String PASSWORD = "123";
  private static final String JDBC_CONFIG_FILE = "classpath:jdbc.properties";

  @Before
  public void before(){
    JdbcConfiguration configuration = JdbcConfiguration.load(JDBC_CONFIG_FILE);
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl(configuration.getUrl());
    dataSource.setUsername(configuration.getUsername());
    dataSource.setPassword(configuration.getPassword());
    userManager = new JdbcBasedUserManager(dataSource);
    jdbcTemplate = userManager.getJdbcTemplate();
    jdbcTemplate.deleteAll(JdbcBasedUserManager.USER_TABLE_NAME);
  }

  @After
  public void after(){
    jdbcTemplate.deleteAll(JdbcBasedUserManager.USER_TABLE_NAME);
  }

  @Test
  public void createAndFind(){
    User user = new User(USERNAME, PASSWORD);
    userManager.create(user);
    final User result = userManager.find(USERNAME);

    Assert.assertEquals(user.getUsername(), result.getUsername());
    Assert.assertEquals(user.getPassword(), result.getPassword());
  }

}
