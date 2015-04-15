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
import java.net.Socket;

import fucks.common.io.StreamPipe;

public class SingleSocketPipe extends StreamPipe{

	private Socket socketFrom;

	private Socket socketTo;

	public SingleSocketPipe(Socket socketFrom, Socket socketTo) throws IOException{
		super(socketFrom.getInputStream(), socketTo.getOutputStream());
		this.socketFrom = socketFrom;
		this.socketTo = socketTo;
	}

	@Override
	public boolean stop() {
		

		try {
			if (!socketFrom.isClosed()) {
				socketFrom.close();
			}
			if (!socketTo.isClosed()) {
				socketTo.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return super.stop();
		
	}

	@Override
	protected int doTransfer(byte[] buffer) {
		if(socketFrom.isClosed()|| socketTo.isClosed()){
			stop();
		}
		return super.doTransfer(buffer);
	}
	
	
	
	


}
