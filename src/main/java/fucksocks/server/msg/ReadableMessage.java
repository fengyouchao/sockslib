/*
 * Copyright 2015-2025 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package fucksocks.server.msg;

import java.io.IOException;
import java.io.InputStream;

import fucksocks.common.SocksException;
import fucksocks.server.Session;

/**
 * The interface <code>ReadableMessage</code> represents a message that can be read by
 * {@link Session}.
 * 
 * @author Youchao Feng
 * @date Apr 5, 2015 10:35:12 AM
 * @version 1.0
 *
 */
public interface ReadableMessage extends Message {

  public void read(InputStream inputStream) throws SocksException, IOException;

}
