package fucksocks.common.http;

/**
 * The class <code>HttpResponseMessage</code> represents HTTP response message.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 10,2015 10:55 PM
 */
public class HttpResponseMessage {

  private String statusLine;
  private HttpHeaders headers;
  private HttpBody body;

  public HttpBody getBody() {
    return body;
  }

  public void setBody(HttpBody body) {
    this.body = body;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public void setHeaders(HttpHeaders headers) {
    this.headers = headers;
  }

  public String getStatusLine() {
    return statusLine;
  }

  public void setStatusLine(String statusLine) {
    this.statusLine = statusLine;
  }
}
