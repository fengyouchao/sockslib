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

package fucks.common.io;

import java.io.IOException;
import java.net.Socket;


/**
 * The class <code>SocketPipe</code> represents pipe that can transfer data from one 
 * socket to another socket. The tow socket should be connected sockets. if any of the 
 * them occurred error the pipe will close all of them. 
 *
 * @author Youchao Feng
 * @date Apr 15, 2015 10:46:03 AM
 * @version 1.0
 *
 */
public class SocketPipe implements Pipe{

	private Pipe pipe1;

	private Pipe pipe2;

	private Socket socket1;

	private Socket socket2;

	public SocketPipe(Socket socket1, Socket socket2) throws IOException {
		if(socket1.isClosed() || socket2.isClosed()){
			throw new IllegalArgumentException("socket should be connected");
		}
		this.socket1 = socket1;
		this.socket2 = socket2;
		pipe1 = new StreamPipe(socket1.getInputStream(), socket2.getOutputStream());
		((StreamPipe)pipe1).setTag(0);
		pipe2 = new StreamPipe(socket2.getInputStream(), socket1.getOutputStream());
		((StreamPipe)pipe2).setTag(1);

		pipe1.addPipeListener(new PipeListnerImp());
		pipe2.addPipeListener(new PipeListnerImp());
	}

	@Override
	public boolean start() {
		pipe1.start();
		pipe2.start();
		return true;
	}

	@Override
	public boolean stop() {

		if (pipe1.stop() ||  pipe2.stop()){
			try {
				if(!pipe1.isRunning() && !pipe2.isRunning()) {
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket1.close();
					socket2.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Socket pipe stoped");

		}
		return true;
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void setBufferSize(int bufferSize) {
		pipe1.setBufferSize(bufferSize);
		pipe2.setBufferSize(bufferSize);
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addPipeListener(PipeListener pipeListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePipeListener(PipeListener pipeListener) {

	}

	private class PipeListnerImp implements PipeListener {

		@Override
		public void onClosed(Pipe pipe) {
			System.out.println("on Closed: pipe tag:"+((StreamPipe)pipe).getTag());
			stop();
		}

		@Override
		public void onStarted(Pipe pipe) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTransfered(Pipe pipe, byte[] buffer, int bufferLength) {
			// TODO Auto-generated method stub

		}

	}



}
