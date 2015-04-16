# Fucksocks

**Fucksocks** a Java implementation of **SOCKS5** protocol. This project is under developing.

## Quick start

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

### SCOKS5 Server(Only support CONNECT method now)

```java
    SocksProxyServer proxyServer = new GenericSocksProxyServer(Socks5Handler.class);
    proxyServer.start(1080);// Creat a SOCKS5 server bind at port 1080
```
