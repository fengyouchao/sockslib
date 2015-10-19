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

package fucksocks.common.http;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class <code>HttpRequestMessage</code> represents HTTP request message.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 10,2015 13:39 PM
 */
public class HttpRequestMessage {

  private String requestLine;
  private HttpHeaders headers;
  private HttpBody body;
  private String method;

  public HttpRequestMessage(String method, HttpHeaders headers, @Nullable HttpBody body) {
    this.requestLine = checkNotNull(requestLine);
    this.method = checkNotNull(method);
    this.headers = checkNotNull(headers);
    this.body = body;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public void setHeaders(HttpHeaders headers) {
    this.headers = checkNotNull(headers);
  }

  public HttpBody getBody() {
    return body;
  }

  public void setBody(@Nullable HttpBody body) {
    this.body = body;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getRequestLine() {
    return requestLine;
  }

  public void setRequestLine(String requestLine) {
    this.requestLine = checkNotNull(requestLine);
  }
}
