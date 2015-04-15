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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class <code>StreamPipe</code> represents a pipe the can transfer stream from a 
 * input stream to a output stream.
 * 
 * @author Youchao Feng
 * @date Apr 6, 2015 11:37:16 PM
 * @version 1.0
 *
 */
public class StreamPipe implements Runnable, Pipe{

	private int tag;

	protected static final Logger logger = LoggerFactory.getLogger(StreamPipe.class);

	/**
	 * Default buffer size.
	 */
	private static final int BUFFER_SIZE = 4096;


	/**
	 * Listeners
	 */
	private List<PipeListener> pipeListeners;

	/**
	 * Input stream.
	 */
	private InputStream from;

	/**
	 * Output stream.
	 */
	private OutputStream to;

	/**
	 * Buffer size.
	 */
	private int bufferSize = BUFFER_SIZE;

	/**
	 * Running thread.
	 */
	private Thread runningThread;

	/**
	 * A flag.
	 */
	private boolean running = false;


	/**
	 * Constructs a Pipe instance with a input stream and a output stream.
	 * 
	 * @param from	stream where it comes from.
	 * @param to stream where it will be transfered to.
	 */
	public StreamPipe(InputStream from, OutputStream to){
		this.from = from;
		this.to = to;
		pipeListeners = new ArrayList<>();
	}

	@Override
	public boolean start(){
		if(!running){	// If the pipe is not running, run it.
			running = true;
			runningThread = new Thread(this);
			runningThread.start();
			for(PipeListener listener: pipeListeners){
				listener.onStarted(this);
			}
			return true;
		}
		return false;
	}


	@Override
	public boolean stop(){
		if(running){	//if the pipe is working, stop it.
			running = false;
			if(runningThread != null){
				runningThread.interrupt();
			}
			for(PipeListener listener: pipeListeners){
				listener.onClosed(this);
			}
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[bufferSize];

		while ( running ) {
			int size =  doTransfer(buffer);
			for(PipeListener listener : pipeListeners){
				listener.onTransfered(this, buffer, size);
			}
			if(size == -1){
				System.out.println("======================================="+tag);
				stop();
				break;
			}
		}
	}

	/**
	 * transfer a buffer.
	 * 
	 * @param buffer Buffer that transfer once.
	 * @return number of byte that transfered.
	 */
	protected int doTransfer(byte[] buffer){

		int length = -1;
		try {
			System.out.println(tag+"===");
			length = from.read(buffer);
			if (length > 0) {	//transfer the buffer to output stream.
				to.write(buffer, 0, length);
				to.flush();
			}

		} catch (IOException e) {
			System.out.println("ex:"+tag);
			e.printStackTrace();
		}

		return length;
	}

	/* (non-Javadoc)
	 * @see fucksocks.server.Pipe#getBufferSize()
	 */
	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	/* (non-Javadoc)
	 * @see fucksocks.server.Pipe#setBufferSize(int)
	 */
	@Override
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/* (non-Javadoc)
	 * @see fucksocks.server.Pipe#isStop()
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	@Override
	public void addPipeListener(PipeListener pipeListener) {
		pipeListeners.add(pipeListener);
	}

	@Override
	public void removePipeListener(PipeListener pipeListener) {
		pipeListeners.remove(pipeListener);
	}

	public List<PipeListener> getPipeListeners() {
		return pipeListeners;
	}

	public void setPipeListeners(List<PipeListener> pipeListeners) {
		this.pipeListeners = pipeListeners;
	}

}
