package jp.kusumotolab.kgenprog;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWatch extends org.apache.commons.lang3.time.StopWatch {

  private static Logger log = LoggerFactory.getLogger(StopWatch.class);

  public final long timeoutSeconds;

  public StopWatch(final long timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  public boolean isTimeout() {
    log.debug("enter isTimeout()");

    final long elapsedSeconds = this.getTime(TimeUnit.SECONDS);
    return elapsedSeconds > this.timeoutSeconds;
  }

  @Override
  public String toString() {
    log.debug("enter toString()");

    final long time = this.getTime(TimeUnit.SECONDS);
    final long hours = time / 3600;
    final long minutes = (time % 3600) / 60;
    final long seconds = (time % 3600) % 60;

    final StringBuilder text = new StringBuilder();
    if (0 < hours) {
      text.append(hours);
      text.append(" hours ");
    }
    if (0 < minutes) {
      text.append(minutes);
      text.append(" minutes ");
    }
    text.append(seconds);
    text.append(" seconds");

    return text.toString();
  }
}
