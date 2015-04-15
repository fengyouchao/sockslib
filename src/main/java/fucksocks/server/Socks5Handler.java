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

import fucksocks.common.io.Pipe;
import fucksocks.common.io.SocketPipe;
import fucksocks.common.methods.SocksMethod;
import fucksocks.server.msg.CommandMessage;
import fucksocks.server.msg.CommandResponseMessage;
import fucksocks.server.msg.MethodSelectionMessage;
import fucksocks.server.msg.MethodSeleteionResponseMessage;
import fucksocks.server.msg.ServerReply;
import fucksocks.utils.LogMessage;

public class Socks5Handler implements SessionHandler{

	private static final Logger logger =LoggerFactory.getLogger(Socks5Handler.class);

	private static final int VERSION = 0x5;

	private Session session;

	private MethodSelector methodSelector;

	public Socks5Handler() {
		methodSelector = new SocksMethodSelector();
		methodSelector.addSupportMethod(0x00);
	}

	@Override
	public void handle(Session session) {

		MethodSelectionMessage msg = new MethodSelectionMessage();
		session.read(msg);
		logger.debug(LogMessage.bytesToHexString(msg.getBytes()));

		SocksMethod selectedMethod = methodSelector.select(msg);
		logger.debug("Server seleted:{}",Integer.toHexString(selectedMethod.getByte()));
		//send select method.
		session.write(new MethodSeleteionResponseMessage(VERSION, selectedMethod));
		selectedMethod.doMethod(session);

		CommandMessage commandMessage = new CommandMessage();
		session.read(commandMessage);	//Read command request.

		logger.debug("Rquest:{}  {}:{}", commandMessage.getCommand(), commandMessage.getInetAddress(), commandMessage.getPort());

		ServerReply reply = null;
		Socket socket = null;
		InetAddress bindAddress = null;
		int bindPort = 0;

		//set default bind address.
		try {
			byte[] defaultAddress = {0,0,0,0};
			bindAddress = InetAddress.getByAddress(defaultAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}


		/****************************************************************************/
		//do connect
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
			e.printStackTrace();
		}


		session.write(new CommandResponseMessage(VERSION, reply,
				bindAddress, bindPort));

		if(reply != ServerReply.SUCCESSED){		//如果返回失败信息，则退出该方法。
			session.close();
			return;
		}
		//
		//		ConnectionJob connectionJob = new ConnectionJob(socket, session);
		//		connectionJob.start();

		try {
			//			StreamPipe client2ServerPipe = new SingleSocketPipe(session.getSocket(), socket);
			//			client2ServerPipe.setTag(0);
			//			client2ServerPipe.start();
			//			StreamPipe server2ClientPipe = new SingleSocketPipe(socket, session.getSocket());
			//			server2ClientPipe.setTag(1);
			//			server2ClientPipe.start();
			Pipe pipe = new SocketPipe(session.getSocket(), socket);
			pipe.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public void run() {
		handle(session);
	}

}
