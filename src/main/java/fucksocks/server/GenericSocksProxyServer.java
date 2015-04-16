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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fucksocks.common.AnonymousAuthentication;
import fucksocks.common.SocksException;
import fucksocks.common.methods.NoAuthencationRequiredMethod;
import fucksocks.common.methods.SocksMethod;
import fucksocks.common.methods.UsernamePasswordMethod;
import fucksocks.server.filters.FilterChain;


public class GenericSocksProxyServer implements SocksProxyServer, Runnable{

	protected static final Logger logger = LoggerFactory.getLogger( 
			GenericSocksProxyServer.class);

	/**
	 * Number of threads in thread pool.
	 */
	private static final int THREAD_NUMBER = 100;

	/**
	 * Thread pool used to process each connection.
	 */
	private ExecutorService executorService;

	private long nextSessionId = 0;

	/**
	 * Server socket.
	 */
	private ServerSocket serverSocket;

	/**
	 * SOCKS socket handler class.
	 */
	private Class<? extends SocksHandler> socksHandlerClass;

	/**
	 * Sessions that server managed.
	 */
	private Map<Long, Session> sessions;

	/**
	 * A flag. 
	 */
	private boolean stop = false;

	/**
	 * Thread that start the server.
	 */
	private Thread thread;
	
	private Authenticator authenticator;
	
	private MethodSelector methodSelector = new SocksMethodSelector();
	
	private FilterChain filterChain;


	public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass) {
		this(socketHandlerClass, Executors.newFixedThreadPool(THREAD_NUMBER));
	}

	public GenericSocksProxyServer(Class<? extends SocksHandler> socketHandlerClass, 
			ExecutorService executorService){
		this.socksHandlerClass = socketHandlerClass;
		this.executorService = executorService;
		sessions = new HashMap<>();
	}

	@Override
	public void run() {
		while(!stop){
			try {
				Socket socket = serverSocket.accept();
				
				Session session =  new SocksSession(getNextSessionId(), socket, sessions);
				sessions.put(session.getId(), session);
				logger.info("Accept from:{}, allocate session ID[{}]",
						socket.getRemoteSocketAddress(), session.getId());
				
				SocksHandler socksHandler = socksHandlerClass.newInstance();
				
				/**临时实现方式，以后可能更改**/
				if(authenticator != null){
					UsernamePasswordMethod.setAuthenticator(
							(UsernamePasswordAuthenticator)authenticator);
					methodSelector.addSupportMethod(new UsernamePasswordMethod());
				}
				
				/*initialize socks handler*/
				socksHandler.setSession(session);
				socksHandler.setMethodSelector(methodSelector);
				socksHandler.setFilterChain(filterChain);
				
				executorService.execute(socksHandler);
//				thread.start();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IOException e) {
				//Catches the exception that cause by shutdown method.
				if(e.getMessage().equals("Socket closed") && stop){
					logger.debug("Server shutdown");
					return;
				}
				e.printStackTrace();
			}
		}
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public void shutdown() {
		stop = true;
		if (thread != null) {
			thread.interrupt();
		}
		try {
			closeAllSession();
			if(serverSocket != null && serverSocket.isBound()){
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void start() throws SocksException, IOException {
		start(DEFAULT_SOCKS_PORT);
	}

	@Override
	public void start(int bindPort) throws SocksException, IOException {
		serverSocket = new ServerSocket(bindPort);
		thread = new Thread(this);
		thread.start();

		logger.info("Create proxy server at port:{}", bindPort);
	}

	/**
	 * Closes all sessions.
	 */
	private void closeAllSession() {
		for(long key: sessions.keySet()){
			sessions.get(key).close();
		}
		
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	private synchronized long getNextSessionId() {
		nextSessionId++;
		return nextSessionId;
	}

	@Override
	public Map<Long, Session> getManagedSessions() {
		return sessions;
	}

	@Override
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	@Override
	public void setSupportedMethod(SocksMethod... methods) {
		methodSelector.setSupportMethod(methods);
	}

}
