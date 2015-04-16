/* 
 * Copyright 2015-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fucksocks.test;

import java.io.IOException;

import fucksocks.common.SocksException;
import fucksocks.server.GenericSocksProxyServer;
import fucksocks.server.Socks5Handler;
import fucksocks.server.SocksProxyServer;
import fucksocks.server.UsernamePasswordAuthenticator;

public class TestProxyServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UsernamePasswordAuthenticator authenticator = new UsernamePasswordAuthenticator();
		authenticator.addUser("socks", "1234");
		
		SocksProxyServer proxyServer = new GenericSocksProxyServer(Socks5Handler.class);
		proxyServer.setAuthenticator(authenticator);
		
		try {
			proxyServer.start();
		} catch (SocksException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		proxyServer.shutdown();
	}

}
