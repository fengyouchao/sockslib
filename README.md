# Fucksocks

**Fucksocks** a Java implementation of **SOCKS4/SOCKS5** protocol. This project is under developing.

## Quick start

### SCOKS5 Client

#### CONNECT

	SocksProxy proxy = new Socks5(new InetSocketAddress("localhost",1080));
	Socket socket = new SocksSocket(proxy, new InetSocketAddress("whois.internic.net",43));

#### UDP ASSOCIATE

	DatagramSocket socket = new Socks5DatagramSocket(proxy);
	