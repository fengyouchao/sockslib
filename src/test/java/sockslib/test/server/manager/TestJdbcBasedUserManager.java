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

package sockslib.test.server.manager;

import sockslib.server.manager.JdbcBasedUserManager;
import sockslib.server.manager.JdbcConfiguration;
import sockslib.server.manager.User;
import sockslib.utils.jdbc.JdbcTemplate;
import junit.framework.Assert;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link JdbcBasedUserManager}
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 09, 2015
 */
public class TestJdbcBasedUserManager {

  private static final String DEFAULT_USERNAME = "fucksocks";
  private static final String DEFAULT_PASSWORD = "123";
  private static final String JDBC_CONFIG_FILE = "classpath:jdbc-h2.properties";
  private static final String SQL_DROP_TABLE = "DROP TABLE `SOCKS_USERS` IF EXISTS";
  private static final String SQL_CREATE_TABLE =
      "CREATE TABLE SOCKS_USERS (`username` varchar(50) PRIMARY KEY, `password` varchar(32))";
  private JdbcTemplate jdbcTemplate;
  private JdbcBasedUserManager userManager;

  @Before
  public void before() {
    JdbcConfiguration configuration = JdbcConfiguration.load(JDBC_CONFIG_FILE);
    BasicDataSource dataSource = new BasicDataSource();
    assert configuration != null;
    dataSource.setUrl(configuration.getUrl());
    dataSource.setUsername(configuration.getUsername());
    dataSource.setPassword(configuration.getPassword());
    userManager = new JdbcBasedUserManager(dataSource);
    jdbcTemplate = userManager.getJdbcTemplate();
    jdbcTemplate.execute(SQL_DROP_TABLE);
    jdbcTemplate.execute(SQL_CREATE_TABLE);
  }

  @After
  public void after() {
    jdbcTemplate.execute(SQL_DROP_TABLE);
  }

  @Test
  public void createAndFind() {
    User user = new User(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    userManager.create(user);
    final User result = userManager.find(DEFAULT_USERNAME);

    Assert.assertEquals(user.getUsername(), result.getUsername());
    Assert.assertEquals(user.getPassword(), result.getPassword());
  }

}
