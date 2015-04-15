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



/**
 * The class <code>Pipe</code> represents pipe that can transfer byte from a input
 * stream to a output stream.
 *
 * @author Youchao Feng
 * @date Apr 15, 2015 9:31:29 AM
 * @version 1.0
 *
 */
public interface Pipe {

	/**
	 * Start the pipe, the pipe will work with a new thread.
	 * @return TODO
	 */
	boolean start();

	/**
	 * Stop the pipe, the pipe will stop its work, but it don't close the 
	 * input stream or output stream.
	 * @return TODO
	 */
	boolean stop();

	int getBufferSize();

	void setBufferSize(int bufferSize);

	boolean isRunning();
	
	void addPipeListener(PipeListener pipeListener);
	
	void removePipeListener(PipeListener pipeListener);

}