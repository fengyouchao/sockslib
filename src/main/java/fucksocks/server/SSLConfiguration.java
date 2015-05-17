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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.common.KeyStoreInfo;

/**
 * The class <code>SSLConfiguration</code> represents a configuration of SSL.
 * 
 * @author Youchao Feng
 * @date May 17, 2015 6:52:52 PM
 * @version 1.0
 *
 */
public class SSLConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(SSLConfiguration.class);

  private KeyStoreInfo keyStoreInfo;
  private KeyStoreInfo trustKeyStoreInfo;
  private boolean needClientAuth = false;

  public SSLConfiguration(KeyStoreInfo keyStoreInfo, KeyStoreInfo trustKeyStoreInfo) {
    this(keyStoreInfo, trustKeyStoreInfo, false);
  }

  public SSLConfiguration(KeyStoreInfo keyStoreInfo, KeyStoreInfo trustKeyStoreInfo,
      boolean clientAuth) {
    this.keyStoreInfo = keyStoreInfo;
    this.trustKeyStoreInfo = trustKeyStoreInfo;
    this.needClientAuth = clientAuth;
  }

  public static SSLConfiguration load(String filePath) throws FileNotFoundException, IOException {

    logger.debug("load SSL configuration file:{}", filePath);

    Properties properties = new Properties();
    properties.load(new FileInputStream(filePath));

    String keystorePath = getAbstractPath(properties.getProperty("fucksocks.server.ssl.keystore"));
    String password = properties.getProperty("fucksocks.server.ssl.keystore.password");
    String trustKeystorePath =
        getAbstractPath(properties.getProperty("fucksocks.server.ssl.trust.keystore"));
    String trustPassword = properties.getProperty("fucksocks.server.ssl.trust.keystore.password");

    KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keystorePath, password);
    KeyStoreInfo trustKeyStoreInfo = new KeyStoreInfo(trustKeystorePath, trustPassword);
    String clientAuthValue = properties.getProperty("fucksocks.server.ssl.client.auth");
    boolean clientAuth = false;
    if (clientAuthValue.equalsIgnoreCase("true")) {
      clientAuth = true;
    }

    return new SSLConfiguration(keyStoreInfo, trustKeyStoreInfo, clientAuth);
  }

  private static String getAbstractPath(String path) throws FileNotFoundException {
    if (path.startsWith("classpath:")) {
      String classPathValue = path.split(":")[1];
      if (!classPathValue.startsWith(File.separator)) {
        classPathValue = File.separator + classPathValue;
      }

      URL url = SSLConfiguration.class.getResource(classPathValue);
      if(url == null){
        throw new FileNotFoundException(path);
      }
      return url.getPath();
    }
    return path;
  }

  public static SSLConfiguration loadClassPath(String filePath) throws FileNotFoundException,
      IOException {

    if (!filePath.startsWith(File.separator)) {
      filePath = File.separator + filePath;
    }
    URL url = SSLConfiguration.class.getResource(filePath);
    if(url == null){
      throw new FileNotFoundException("classpath:"+filePath);
    }
    String path = url.getPath();
    return load(path);
  }

  public KeyStoreInfo getKeyStoreInfo() {
    return keyStoreInfo;
  }

  public void setKeyStoreInfo(KeyStoreInfo keyStoreInfo) {
    this.keyStoreInfo = keyStoreInfo;
  }

  public KeyStoreInfo getTrustKeyStoreInfo() {
    return trustKeyStoreInfo;
  }

  public void setTrustKeyStoreInfo(KeyStoreInfo trustKeyStoreInfo) {
    this.trustKeyStoreInfo = trustKeyStoreInfo;
  }

  public SSLServerSocketFactory getSSLServerSocketFactory() throws SSLConfigurationException {

    String KEY_STORE_PASSWORD = getKeyStoreInfo().getPassword();
    String TRUST_KEY_STORE_PASSWORD = getTrustKeyStoreInfo().getPassword();
    String KEY_STORE_PATH = getKeyStoreInfo().getKeyStorePath();
    String TRUST_KEY_STORE_PATH = getTrustKeyStoreInfo().getKeyStorePath();

    try {
      SSLContext ctx = SSLContext.getInstance("SSL");

      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");

      KeyStore keyStore = KeyStore.getInstance("JKS");
      KeyStore trustKeyStore = KeyStore.getInstance("JKS");

      keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
      trustKeyStore.load(new FileInputStream(TRUST_KEY_STORE_PATH),
          TRUST_KEY_STORE_PASSWORD.toCharArray());

      keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
      trustManagerFactory.init(trustKeyStore);

      ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

      return ctx.getServerSocketFactory();
    } catch (Exception e) {
      throw new SSLConfigurationException(e.getMessage());
    }

  }

  public boolean isNeedClientAuth() {
    return needClientAuth;
  }

  public void setNeedClientAuth(boolean needClientAuth) {
    this.needClientAuth = needClientAuth;
  }

}
