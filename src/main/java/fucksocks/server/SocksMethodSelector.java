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

import java.util.HashSet;
import java.util.Set;

import fucksocks.common.methods.NoAcceptableMethod;
import fucksocks.common.methods.SocksMethod;
import fucksocks.common.methods.SocksMethodRegistry;
import fucksocks.server.msg.MethodSelectionMessage;


/**
 * 
 * The class <code>SocksMethodSelector</code> implements the {@link MethodSelector}.
 * 
 * @author Youchao Feng
 * @date Apr 7, 2015 10:24:43 AM
 * @version 1.0
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc1928.txt">SOCKS Protocol Version 5</a>
 *
 */
public class SocksMethodSelector implements MethodSelector{
	
	private Set<Integer> supportMethods;
	
	public SocksMethodSelector() {
		supportMethods = new HashSet<>();
	}

	@Override
	public SocksMethod select(MethodSelectionMessage message) {
		int[] methods = message.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (supportMethods.contains(methods[i])) {
				try {
					return SocksMethodRegistry.getByByte((byte)methods[i]).newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return new NoAcceptableMethod();
	}

	@Override
	public Set<Integer> getSupportMethods() {
		return supportMethods;
	}

	@Override
	public void setSupportMethods(Set<Integer> supportMethods) {
		this.supportMethods = supportMethods;
	}
	
	@Override
	public void addSupportMethod(int method){
		supportMethods.add(method);
	}
	
	@Override
	public void removeSupportMethod(int method){
		supportMethods.remove(method);
	}
	
	@Override
	public void clearAllSupportMethods(){
		supportMethods.clear();
	}

	@Override
	public void addSupportMethod(SocksMethod socksMethod) {
		supportMethods.add(socksMethod.getByte());
	}

	@Override
	public void setSupportMethod(SocksMethod... methods) {
		supportMethods.clear();
		for (int i = 0; i < methods.length; i++) {
			supportMethods.add(methods[i].getByte());
		}
		
	}
	
	

}
