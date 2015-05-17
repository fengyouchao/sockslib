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

package fucksocks.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.client.SocksProxy;
import fucksocks.client.SocksSocket;
import fucksocks.common.KeyStoreInfo;
import fucksocks.server.SSLConfiguration;

/**
 * The class <code>SocksClientSSLUtil</code> represents an Socks client SSL utility.
 * 
 * @author Youchao Feng
 * @date May 17, 2015 11:39:55 PM
 * @version 1.0
 *
 */
public class SocksClientSSLUtil {

  private static final Logger logger = LoggerFactory.getLogger(SocksClientSSLUtil.class);

  private SSLSocketFactory socketFactory;

  private SocksClientSSLUtil(SSLSocketFactory socketFactory) {
    this.socketFactory = socketFactory;
  }

  public SocksSocket create(SocksProxy proxy) throws IOException {
    Socket socket = socketFactory.createSocket();
    SocksSocket s = new SocksSocket(proxy, socket);
    return s;
  }

  public static SocksClientSSLUtil newInstance(KeyStoreInfo keyStoreInfo,
      KeyStoreInfo trustKeyStoreInfo) throws Exception {
    return new SocksClientSSLUtil(getSSLSocketFactory(keyStoreInfo, trustKeyStoreInfo));
  }

  public static SocksClientSSLUtil load(String filePath) throws FileNotFoundException, IOException,
      Exception {
    logger.debug("load ssl cnofigruration file:{}", filePath);

    Properties properties = new Properties();
    properties.load(new FileInputStream(filePath));

    String keystorePath = getAbstractPath(properties.getProperty("fucksocks.client.ssl.keystore"));
    String password = properties.getProperty("fucksocks.client.ssl.keystore.password");
    String trustKeystorePath =
        getAbstractPath(properties.getProperty("fucksocks.client.ssl.trust.keystore"));
    String trustPassword = properties.getProperty("fucksocks.client.ssl.trust.keystore.password");

    KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keystorePath, password);
    KeyStoreInfo trustKeyStoreInfo = new KeyStoreInfo(trustKeystorePath, trustPassword);

    return new SocksClientSSLUtil(getSSLSocketFactory(keyStoreInfo, trustKeyStoreInfo));
  }

  public static SSLSocketFactory getSSLSocketFactory(KeyStoreInfo keyStoreInfo,
      KeyStoreInfo trustKeyStoreInfo) throws NoSuchAlgorithmException, KeyStoreException,
      CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException,
      KeyManagementException {
    String CLIENT_KEY_STORE_PASSWORD = keyStoreInfo.getPassword();
    String CLIENT_TRUST_KEY_STORE_PASSWORD = trustKeyStoreInfo.getPassword();

    SSLContext ctx = SSLContext.getInstance("SSL");

    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

    KeyStore ks = KeyStore.getInstance("JKS");
    KeyStore tks = KeyStore.getInstance("JKS");

    ks.load(new FileInputStream(keyStoreInfo.getKeyStorePath()),
        CLIENT_KEY_STORE_PASSWORD.toCharArray());
    tks.load(new FileInputStream(trustKeyStoreInfo.getKeyStorePath()),
        CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

    kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
    tmf.init(tks);

    ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    return ctx.getSocketFactory();

  }

  public static SocksClientSSLUtil loadClassPath(String filePath) throws FileNotFoundException,
      IOException, Exception {
    if (!filePath.startsWith(File.separator)) {
      filePath = File.separator + filePath;
    }
    URL url = SocksClientSSLUtil.class.getResource(filePath);
    if (url == null) {
      throw new FileNotFoundException("classpath:" + filePath);
    }
    return load(url.getPath());
  }



  private static String getAbstractPath(String path) throws FileNotFoundException {
    if (path.startsWith("classpath:")) {
      String classPathValue = path.split(":")[1];
      if (!classPathValue.startsWith(File.separator)) {
        classPathValue = File.separator + classPathValue;
      }

      URL url = SSLConfiguration.class.getResource(classPathValue);
      if (url == null) {
        throw new FileNotFoundException(path);
      }
      return url.getPath();
    }
    return path;
  }
}
