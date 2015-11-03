package sockslib.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 30, 2015 10:31 AM
 */
public class Timer {

  private static final Logger logger = LoggerFactory.getLogger(Timer.class);

  private long startTime;
  private long endTime;

  private Timer(long startTime) {
    this.startTime = startTime;
  }

  public static Timer start() {
    return new Timer(System.currentTimeMillis());
  }

  public static void open() {
    Runtime.getRuntime().addShutdownHook(new TimerThread(start()));
  }

  public long stop() {
    endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getTotalTime() {
    return endTime - startTime;
  }


}


