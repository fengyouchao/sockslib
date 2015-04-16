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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;

import fucksocks.common.SocksCommand;
import fucksocks.utils.SocksUtil;

/**
 * 
 * The class <code>RequestCommandMessage</code> represents
 * 
 * @author Youchao Feng
 * @date Apr 6, 2015 11:10:12 AM
 * @version 1.0
 *
 */
public class CommandMessage implements ReadableMessage{
	
	protected static final int ATYPE_IPV4 = 0x01;
	protected static final int ATYPE_DOMAINNAME = 0x03;
	protected static final int ATYPE_IPV6 = 0x04;
	protected static final int CMD_CONNECT = 0x01;
	protected static final int CMD_BIND = 0x02;
	protected static final int CMD_UDP_ASSOCIATE = 0x03;
	
	private int version;
	
	private InetAddress inetAddress;
	
	private int port;
	
	private String host;
	
	private SocksCommand command;
	
	private int reserved;
	
	private int addressType;

	@Override
	public byte[] getBytes() {
		byte[] bytes = new byte[10];
		bytes[0] = (byte)version;
		bytes[1] = (byte)command.getValue();
		bytes[2] = 0;
		bytes[3] = (byte)addressType;
		byte[] addressBytes = inetAddress.getAddress();
		for(int i = 0; i < addressBytes.length; i++){
			bytes[i+4] = addressBytes[i];
		}
		bytes[8] = SocksUtil.getFisrtByteFromPort(port);
		bytes[9] = SocksUtil.getSecondByteFromPort(port);
		
		return bytes;
	}

	@Override
	public void read(InputStream inputStream) {
		try {
			
			version = inputStream.read();
			int cmd = inputStream.read();
			
			switch (cmd) {
			case CMD_CONNECT:
				command = SocksCommand.CONNECT;
				break;
			case CMD_BIND:
				command = SocksCommand.BIND;
				break;
			case CMD_UDP_ASSOCIATE:
				command = SocksCommand.UDP_ASSOCIATE;
				break;

			default:
				break;
			}
			reserved = inputStream.read();
			addressType = inputStream.read();
			
			//read address
			switch (addressType) {
			
			case ATYPE_IPV4:
				byte[] addressBytes = new byte[4];
				inputStream.read(addressBytes);
				inetAddress = InetAddress.getByAddress(addressBytes);
				break;
				
			case ATYPE_DOMAINNAME:
				int domainLength = inputStream.read();
				byte[] domainBytes = new byte[domainLength];
				for(int i = 0; i < domainBytes.length; i++){
					domainBytes[i] = (byte) inputStream.read();
				}
				host = new String(domainBytes, Charset.forName("UTF-8"));
				inetAddress = InetAddress.getByName(host);
			default:
				break;
			}
			
			//Read port
			byte[] portBytes = new byte[2];
			inputStream.read(portBytes);
			port = SocksUtil.bytesToPort(portBytes);
			
			
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	public boolean isDomain(){
		return host != null;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public SocksCommand getCommand() {
		return command;
	}

	public void setCommand(SocksCommand command) {
		this.command = command;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

	public int getAddressType() {
		return addressType;
	}

	public void setAddressType(int addressType) {
		this.addressType = addressType;
	}

}
