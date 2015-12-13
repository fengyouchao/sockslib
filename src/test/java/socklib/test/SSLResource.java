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

package socklib.test;

import sockslib.utils.PathUtil;

import java.io.FileNotFoundException;

/**
 * The class <code>SSLResource</code> is a tool class to get SSL information.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 4:27 PM
 */
public final class SSLResource {

  public static final String PASSWORD = "123456";

  public static String getClientKeyStorePath() throws FileNotFoundException {
    return PathUtil.getAbstractPath("classpath:client-ssl-config/client.jks");
  }

  public static String getClientTrustStorePath() throws FileNotFoundException {
    return PathUtil.getAbstractPath("classpath:client-ssl-config/clientTrust.jks");
  }

  public static String getServerKeyStorePath() throws FileNotFoundException {
    return PathUtil.getAbstractPath("classpath:server-ssl-config/server.jks");
  }

  public static String getServerTrustStorePath() throws FileNotFoundException {
    return PathUtil.getAbstractPath("classpath:server-ssl-config/serverTrust.jks");
  }

  public static String getServerSSLConfigPath() throws FileNotFoundException {
    return PathUtil.getAbstractPath("classpath:server-ssl.properties");
  }

  public static String getClientSSLConfigPath() throws FileNotFoundException {
    return PathUtil.getAbstractPath("classpath:client-ssl.properties");
  }
}
