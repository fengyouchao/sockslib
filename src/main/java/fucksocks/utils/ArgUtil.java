package fucksocks.utils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class <code>ArgUtil</code> is an argument tool class.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 24, 2015 10:20 AM
 */
public class ArgUtil {

  private final String[] args;
  private final Map<String, Integer> argPositionMap;

  public ArgUtil(final String[] args) {
    this.args = args;
    argPositionMap = new HashMap<>();
    for (int i = 0; i < args.length; i++) {
      argPositionMap.put(args[i], i);
    }
  }

  public boolean hasArg(String arg) {
    return argPositionMap.get(arg) != null;
  }

  public boolean hasArgsIn(String... args) {
    for (String arg : args) {
      if (argPositionMap.get(arg) != null) {
        return true;
      }
    }
    return false;
  }

  public String getValue(String arg, @Nullable String defaultValue) {
    Integer index = argPositionMap.get(checkNotNull(arg));
    if (index == null) {
      return defaultValue;
    } else if (index + 1 < args.length) {
      return args[index + 1];
    }
    return defaultValue;
  }

  public long getLongValue(String arg, long defaultValue) {
    String value = getValue(arg, null);
    if (value == null) {
      return defaultValue;
    }
    return Long.parseLong(value);
  }

  public int getIntValue(String arg, int defaultValue) {
    String value = getValue(arg, null);
    if (value == null) {
      return defaultValue;
    }
    return Integer.parseInt(value);
  }

  public boolean getBooleanValue(String arg, boolean defaultValue) {
    String value = getValue(arg, null);
    if (value == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(value);
  }

  public String getValueFromArg(String prefix, String splitRegex, @Nullable String defaultValue) {
    checkNotNull(prefix);
    checkNotNull(splitRegex);
    for (String arg : args) {
      if (arg.startsWith(prefix)) {
        String[] nameValue = arg.split(splitRegex);
        if (nameValue.length >= 2) {
          return nameValue[1];
        } else {
          return defaultValue;
        }
      }
    }
    return defaultValue;
  }

  public int getIntValueFromArg(String prefix, String splitRegex, int defaultValue) {
    String value = getValueFromArg(prefix, splitRegex, null);
    if (value == null) {
      return defaultValue;
    }
    return Integer.parseInt(value);
  }

  public long getLongValueFromArg(String prefix, String splitRegex, long defaultValue) {
    String value = getValueFromArg(prefix, splitRegex, null);
    if (value == null) {
      return defaultValue;
    }
    return Long.parseLong(value);
  }

  public boolean getBooleanValueFromArg(String prefix, String splitRegex, boolean defaultValue) {
    String value = getValueFromArg(prefix, splitRegex, null);
    if (value == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(value);
  }

  public String[] getArgs() {
    return args;
  }

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
