package sockslib.utils;

import static com.google.common.base.Preconditions.checkNotNull;

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
   * @param bytes Bytes array.
   * @param type  Message type.
   * @return Log message.
   */
  public static String build(byte[] bytes, MsgType type) {
    return build(bytes, bytes.length, type);
  }

  /**
   * Returns a log message.
   *
   * @param bytes Bytes array.
   * @param size  data length in bytes array.
   * @param type  Message type.
   * @return Log message.
   */
  public static String build(byte[] bytes, final int size, MsgType type) {
    checkNotNull(bytes, "Argument [bytes] may not be null");
    checkNotNull(type, "Argument [type] may not be null");
    StringBuilder debugMsg = new StringBuilder();
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
      int x = UnsignedByte.toInt(bytes[i]);
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
    checkNotNull(bytes, "Argument [bytes] may not be null");
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
