package example;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class Baz {

  public int baz(int n) {
    if (n > 0) {
      n = Bar.bar1(n);
    } else {
      n = Bar.bar2(n);
    }
    // 内部クラスの生成と実行
    InnerClass innerClass = new InnerClass();
    innerClass.exec();

    // 静的内部クラスの実行
    StaticInnerClass.exec();

    // 外部クラスの生成と実行
    OuterClass outerClass = new OuterClass();
    outerClass.exec();

    // 無名クラスの生成と利用
    PrintStream printStream = new PrintStream(new PrintStream(new OutputStream() {

      @Override
      public void write(int b) {
        b++; // do nothing but be measured by jacoco
      }
    }));
    printStream.write(3); // call anonymous class
    printStream.close();

    // ラムダの利用
    Arrays.asList("xxx")
        .stream()
        .map(s -> s.toString());

    return n;
  }

  // a simple inner class
  class InnerClass {

    void exec() {
      new String(); // do nothing but be measured by jacoco
    }
  }

  // a static inner class
  static class StaticInnerClass {

    static void exec() {
      new String(); // do nothing but be measured by jacoco
    }
  }
}


class OuterClass {

  void exec() {
    new String(); // do nothing but be measured by jacoco
  }

}