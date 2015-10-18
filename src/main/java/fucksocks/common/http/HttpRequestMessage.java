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
