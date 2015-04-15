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

package fucksocks.server;

import java.io.IOException;

import fucksocks.common.SocksException;


/**
 * The interface <code>SocksProxyServer</code> represents
 * a SOCKS server.
 * 
 * @author Youchao Feng
 * @date  Mar 25, 2015 10:07:29 AM 
 * @version 1.0
 */
public interface SocksProxyServer {
	
	/**
	 * Starts a SOCKS server bind a default port. 
	 * 
	 * @throws SocksException	If any error about SOCKS protocol occurs.
	 * @throws IOException		If any I/O error occurs.
	 */
	void start() throws SocksException, IOException;
	
	/**
	 * Starts a SOCKS server and binds a port.
	 * 
	 * @param bindPort			The port that SOCKS server listened..
	 * @throws SocksException	If any error about SOCKS protocol occurs.
	 * @throws IOException		If any I/O error occurs.
	 */
	void start(int bindPort) throws SocksException, IOException;
	
	/**
	 * Shutdown a SOCKS server.
	 */
	void shutdown();
	
	static final int DEFAULT_SOCKS_PORT = 1080;

}
