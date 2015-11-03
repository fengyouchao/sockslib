package sockslib.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 30, 2015 11:14 AM
 */
public class TimerThread extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(TimerThread.class);
  private Timer timer;

  public TimerThread(Timer timer) {
    this.timer = timer;
  }

  @Override
  public void run() {
    logger.info("Total Run Time: {}ms", timer.stop());
  }
}
