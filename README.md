# Fucksocks

**Fucksocks** a Java implementation of **SOCKS5** protocol. This project is under developing.

## Quick start

#### Dependency

You should put following libraries in your project's *CLASSPATH*:

* log4j-1.2.17.jar
* slf4j-log4j12-1.7.5.jar
* slf4j-api-1.7.5.jar

### SCOKS5 Client

#### CONNECT

```java
    SocksProxy proxy = new Socks5(new InetSocketAddress("localhost",1080));
    Socket socket = new SocksSocket(proxy, new InetSocketAddress("whois.internic.net",43));
```

#### BIND

```java
    SocksServerSocket serverSocket = new SocksServerSocket(proxy, inetAddress,8080);
    InetAddress bindAddress = serverSocket.getBindAddress();
    int bindPort  = serverSocket.getBindPort();
    Socket socket = serverSocket.accept();
```

#### UDP ASSOCIATE

```java
   DatagramSocket socket = new Socks5DatagramSocket(proxy);
```

### SCOKS5 Server

```java
     SocksProxyServer proxyServer = SocksProxyServerFactory.newNoAuthenticaionServer();
     proxyServer.start();// Creat a SOCKS5 server bind at port 1080
```
