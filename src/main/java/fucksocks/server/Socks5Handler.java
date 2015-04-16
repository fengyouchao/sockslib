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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.common.NotImplementException;
import fucksocks.common.SocksException;
import fucksocks.common.io.Pipe;
import fucksocks.common.io.SocketPipe;
import fucksocks.common.methods.SocksMethod;
import fucksocks.server.filters.FilterChain;
import fucksocks.server.msg.CommandMessage;
import fucksocks.server.msg.CommandResponseMessage;
import fucksocks.server.msg.MethodSelectionMessage;
import fucksocks.server.msg.MethodSeleteionResponseMessage;
import fucksocks.server.msg.ServerReply;
import fucksocks.utils.LogMessage;

/**
 * 
 * The class <code>Socks5Handler</code> represents a handler that can handle SOCKS5 
 * protocol.
 *
 * @author Youchao Feng
 * @date Apr 16, 2015 11:03:49 AM
 * @version 1.0
 *
 */
public class Socks5Handler implements SocksHandler{

	/**
	 * Logger
	 */
	private static final Logger logger =LoggerFactory.getLogger(Socks5Handler.class);

	/**
	 * Protocol version.
	 */
	private static final int VERSION = 0x5;

	/**
	 * Session.
	 */
	private Session session;

	/**
	 * Method selector.
	 */
	private MethodSelector methodSelector;

	private FilterChain filterChain;

	@Override
	public void handle(Session session) throws Exception {

		MethodSelectionMessage msg = new MethodSelectionMessage();
		session.read(msg);

		if(msg.getVersion() != VERSION){
			logger.debug("Portocol error, close connection from {}", session.getRemoteAddress());;
			session.close();
			return;
		}
		logger.debug(LogMessage.bytesToHexString(msg.getBytes()));

		SocksMethod selectedMethod = methodSelector.select(msg);
		logger.debug("Server seleted:{}",selectedMethod.getMethodName());
		//send select method.
		session.write(new MethodSeleteionResponseMessage(VERSION, selectedMethod));

		//do method.
		selectedMethod.doMethod(session);


		CommandMessage commandMessage = new CommandMessage();
		session.read(commandMessage);	//Read command request.

		logger.info("Session[{}] send Rquest:{}  {}:{}", 
				session.getId(), commandMessage.getCommand(),
				commandMessage.getInetAddress(), commandMessage.getPort());


		/****************************************************************************/

		switch (commandMessage.getCommand()) {

		case BIND:
			throw new NotImplementException("Not implement BIND command");
		case CONNECT:
			doConnect(session, commandMessage);
			break;
		case UDP_ASSOCIATE:
			throw new NotImplementException("Not implement UDP ASSOCIATE command");
		default:
			throw new NotImplementException("Not support command");

		}



	}

	private void doConnect(Session session, CommandMessage commandMessage) throws Exception{

		ServerReply reply = null;
		Socket socket = null;
		InetAddress bindAddress = null;
		int bindPort = 0;

		//set default bind address.
		byte[] defaultAddress = {0,0,0,0};
		bindAddress = InetAddress.getByAddress(defaultAddress);
		//DO connect
		try {
			if (!commandMessage.isDomain()) {
				socket = new Socket(commandMessage.getInetAddress(), commandMessage.getPort());
			}else{
				socket = new Socket(commandMessage.getHost(), commandMessage.getPort());
			}
			bindAddress = socket.getLocalAddress();
			bindPort = socket.getLocalPort();
			reply = ServerReply.SUCCESSED;

		} catch (IOException e) {
			if( e.getMessage().equals("Connection refused")) {
				reply  = ServerReply.CONNECTION_REFUSED;
			}
			else if (e.getMessage().equals("Operation timed out")) {
				reply = ServerReply.TTL_EXPIRED;
			}
			else if (e.getMessage().equals("Network is unreachable")) {
				reply = ServerReply.NETWORK_UNREACHABLE;
			}
			else if (e instanceof UnknownHostException) {
				reply = ServerReply.NETWORK_UNREACHABLE;
			}
			logger.debug(e.getMessage());
		}


		session.write(new CommandResponseMessage(VERSION, reply,
				bindAddress, bindPort));

		if(reply != ServerReply.SUCCESSED){		//如果返回失败信息，则退出该方法。
			session.close();
			return;
		}

		Pipe pipe = new SocketPipe(session.getSocket(), socket);
		pipe.start();
		
		//wait for pipe exit.
		while(pipe.isRunning()){
			Thread.sleep(1000);
		}

	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}


	@Override
	public void run() {
		try {
			handle(session);
		} catch (Exception e) {
			session.close();
			logger.error(e.getMessage());
			e.printStackTrace();
			logger.info("Session[{}] closed", session.getId());
//			e.printStackTrace();
		}
	}

	@Override
	public FilterChain getFilterChain() {
		return filterChain;
	}

	@Override
	public void setFilterChain(FilterChain filterChain) {
		this.filterChain = filterChain;
	}

	@Override
	public MethodSelector getMethodSelector() {
		return methodSelector;
	}

	@Override
	public void setMethodSelector(MethodSelector methodSelector) {
		this.methodSelector = methodSelector;
	}

}
