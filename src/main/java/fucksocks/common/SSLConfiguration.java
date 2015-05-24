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
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.utils.PathUtil;

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

    String keystorePath =
        PathUtil.getAbstractPath(properties.getProperty("fucksocks.ssl.keystore"));
    String password = properties.getProperty("fucksocks.ssl.keystore.password");
    String type = properties.getProperty("fucksocks.ssl.keystore.type", "JSK");
    String trustKeystorePath =
        PathUtil.getAbstractPath(properties.getProperty("fucksocks.ssl.trust.keystore"));
    String trustPassword = properties.getProperty("fucksocks.ssl.trust.keystore.password");
    String trustType = properties.getProperty("fucksocks.ssl.trust.keystore.type", "JSK");
    KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keystorePath, password, type);
    KeyStoreInfo trustKeyStoreInfo = new KeyStoreInfo(trustKeystorePath, trustPassword, trustType);
    String clientAuthValue = properties.getProperty("fucksocks.ssl.client.auth", "false");
    boolean clientAuth = false;
    if (clientAuthValue.equalsIgnoreCase("true")) {
      clientAuth = true;
    }

    return new SSLConfiguration(keyStoreInfo, trustKeyStoreInfo, clientAuth);
  }

  public static SSLConfiguration loadClassPath(String filePath) throws FileNotFoundException,
      IOException {

    if (!filePath.startsWith(File.separator)) {
      filePath = File.separator + filePath;
    }
    URL url = SSLConfiguration.class.getResource(filePath);
    if (url == null) {
      throw new FileNotFoundException("classpath:" + filePath);
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

  public SSLSocketFactory getSSLSocketFactory() throws SSLConfigurationException {
    try {
      SSLContext context = SSLContext.getInstance("SSL");

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
      KeyStore TrustKeyStory = KeyStore.getInstance("JKS");
      TrustKeyStory.load(new FileInputStream(trustKeyStoreInfo.getKeyStorePath()),
          trustKeyStoreInfo.getPassword().toCharArray());
      trustManagerFactory.init(TrustKeyStory);

      if (keyStoreInfo != null && keyStoreInfo.getKeyStorePath() != null) {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keyStoreInfo.getKeyStorePath()), keyStoreInfo
            .getPassword().toCharArray());
        keyManagerFactory.init(keyStore, keyStoreInfo.getPassword().toCharArray());

        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
            null);
      } else {
        context.init(null, trustManagerFactory.getTrustManagers(), null);
      }
      return context.getSocketFactory();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new SSLConfigurationException(e.getMessage());
    }
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
