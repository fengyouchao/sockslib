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

package sockslib.common.http;

/**
 * The class <code>HttpBody</code> represents HTTP body.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 10,2015 10:40 PM
 */
public class HttpBody {

  /**
   * The data in body of HTTP.
   */
  private byte[] data;

  /**
   * Constructs an instance of {@link HttpBody} with data.
   *
   * @param data data in HTTP body.
   */
  public HttpBody(byte[] data) {
    this.data = data;
  }

  /**
   * Returns data in HTTP body.
   *
   * @return data in HTTP body.
   */
  public byte[] getData() {
    return data;
  }

}
