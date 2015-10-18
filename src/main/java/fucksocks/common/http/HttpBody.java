package fucksocks.common.http;

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
