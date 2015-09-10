package fucksocks.utils;

/**
 * <code>LogMessageBuilder</code> a tool class to generate some debug message.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 24, 2015 5:41:56 PM
 */
public class LogMessageBuilder {

  /**
   * Returns a log message.
   *
   * @param buffer Bytes array.
   * @param type   Message type.
   * @return Log message.
   */
  public static String build(byte[] buffer, MsgType type) {
    return build(buffer, buffer.length, type);
  }

  /**
   * Returns a log message.
   *
   * @param buffer Bytes array.
   * @param size   data length in bytes array.
   * @param type   Message type.
   * @return Log message.
   */
  public static String build(byte[] buffer, final int size, MsgType type) {
    StringBuffer debugMsg = new StringBuffer();

    switch (type) {
      case RECEIVE:
        debugMsg.append("Received: ");
        break;
      case SEND:
        debugMsg.append("Sent: ");
        break;
      default:
        break;

    }

    for (int i = 0; i < size; i++) {
      int x = UnsignedByte.toInt(buffer[i]);
      debugMsg.append(Integer.toHexString(x)).append(" ");
    }
    return debugMsg.toString();
  }

  /**
   * Returns a hex string.
   *
   * @param bytes Bytes array.
   * @return Bytes in hex.
   */
  public static String bytesToHexString(byte[] bytes) {
    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < bytes.length; i++) {
      buffer.append(UnsignedByte.toHexString(bytes[i]));
      if (i < bytes.length - 1) {
        buffer.append(" ");
      }
    }
    return buffer.toString();
  }

  public enum MsgType {
    SEND, RECEIVE
  }

}
