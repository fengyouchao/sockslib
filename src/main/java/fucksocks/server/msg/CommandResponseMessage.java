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

package fucksocks.server.msg;

import java.net.InetAddress;

import fucksocks.utils.SocksUtil;

/**
 * 
 * The class <code>CommandResponseMessage</code> represents
 * 
 * @author Youchao Feng
 * @date Apr 6, 2015 11:10:25 AM
 * @version 1.0
 *
 */
public class CommandResponseMessage implements Message{
	
	private int version = 5;
	
	private int reserved = 0x00;
	
	private int addresssType;
	
	private InetAddress bindAddress;
	
	private int bindPort;
	
	private ServerReply reply;
	
	public CommandResponseMessage(int version, ServerReply reply, InetAddress bindAddress, int bindPort) {
		this.version = version;
		this.reply = reply;
		this.bindAddress = bindAddress;
		this.bindPort = bindPort;
		if(bindAddress.getAddress().length==4){
			addresssType = 0x01;
		}else{
			addresssType = 0x04;
		}
	}

	@Override
	public byte[] getBytes() {
		byte[] bytes = null;
		if(addresssType == 1){
			bytes = new byte[10];
			bytes[0] = (byte)version;
			bytes[1] = reply.getValue();
			bytes[2] = (byte)reserved;
			bytes[3] = (byte)addresssType;
			for (int i = 0; i < bindAddress.getAddress().length; i++) {
				bytes[i+4] = bindAddress.getAddress()[i];
			}
			bytes[8] = SocksUtil.getFisrtByteFromPort(bindPort);
			bytes[9] = SocksUtil.getSecondByteFromPort(bindPort);
		}
		
		// TODO Auto-generated method stub
		return bytes;
	}

}
