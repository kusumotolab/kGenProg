package example;

import java.lang.management.ManagementFactory;

public class Foo {

  public void foo(boolean isLoop) {
    while (isLoop) {
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // スレッド生存確認のためのstdout出力．
      // 呼び出し元のメソッド名（事実上テストメソッド名）とスレッド数を書き出しておく．
      // テスト実行ごとにスレッド数が増える場合，タイムアウトが適用されていないとみなして良い．
      printThreadInfo();
    }
  }

  private void printThreadInfo() {
    final StackTraceElement st = Thread.currentThread().getStackTrace()[3];
    final String caller = st.getClassName() + "." + st.getMethodName();
    final int threadCount = ManagementFactory.getThreadMXBean().getDaemonThreadCount();
    System.out.printf("BS20: %s [%d]\n", caller, threadCount);
  }
}
