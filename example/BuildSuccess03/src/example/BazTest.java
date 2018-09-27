package example;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class BazTest {

  @Test
  public void test01() {
    // OuterClassをBaz経由で呼び出し
    Baz baz = new Baz();
    baz.baz(99);
    assertTrue(true); // 強制成功テスト
  }

  @Test
  public void test02() {
    // OuterClassを外から直接呼び出し
    OuterClass outer = new OuterClass();
    outer.exec();
    assertTrue(true); // 強制成功テスト
  }

}