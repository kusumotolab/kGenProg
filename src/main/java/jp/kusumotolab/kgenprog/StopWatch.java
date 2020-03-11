package jp.kusumotolab.kgenprog;

import java.util.concurrent.TimeUnit;

/**
 * 実行時間の計測を行うためのクラス
 *
 * @author higo
 */
public class StopWatch extends org.apache.commons.lang3.time.StopWatch {

  public final long timeoutSeconds;

  /**
   * コンストラクタ．タイムアウト時間を与えてインスタンスを生成する．
   *
   * @param timeoutSeconds タイムアウト時間
   */
  public StopWatch(final long timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  /**
   * タイムアウト時間に到達しているかどうかを判定する．
   *
   * @return タイムアウト時間に到達している場合はtrue，そうでない場合はfalse
   */
  public boolean isTimeout() {
    final long elapsedSeconds = this.getTime(TimeUnit.SECONDS);
    return elapsedSeconds > this.timeoutSeconds;
  }

  /**
   * このメソッドが呼び出された時点での実行時間の文字列表現を返す．
   *
   * @return このメソッドが呼び出された時点での実行時間の文字列表現
   */
  @Override
  public String toString() {
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
