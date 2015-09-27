package fucksocks.utils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class <code>ArgUtil</code> is an argument tool class.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 24, 2015 10:20 AM
 */
public class ArgUtil {

  public static <T> T valueOf(String arg, Class<T> type) {
    checkNotNull(arg, "Argument [arg] may not be null");
    checkNotNull(type, "Argument [type] may not be null");
    Object value = arg.trim();
    if (arg.contains("=")) {
      value = arg.split("=")[1].trim();
    }
    if (type.equals(String.class)) {

    } else if (type.equals(Integer.class)) {
      value = Integer.parseInt((String) value);
    } else if (type.equals(Long.class)) {
      value = Long.parseLong((String) value);
    } else {
      throw new IllegalArgumentException("Not support" + type.getName());
    }
    return (T) value;
  }

  public static String valueOf(String arg) {
    return valueOf(arg, String.class);
  }

  public static int intValueOf(String arg) {
    return valueOf(arg, Integer.class);
  }

  public static long longValueOf(String arg) {
    return valueOf(arg, Long.class);
  }

  public static boolean isHelpArg(String arg) {
    arg = arg.trim();
    return arg.equals("-h") || arg.equals("--help");
  }
}
