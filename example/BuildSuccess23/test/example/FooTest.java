package example;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FooTest {

  // まずは成功テスト
  @Test
  public void test01() throws Exception {
    new Foo().load("java.lang.String");
  }

  // これも成功
  @Test
  public void test02() throws Exception {
    new Foo().load("example.Foo");
  }

  // 失敗することを期待するテスト．クラスロードできるはずがない
  @Test(expected = ClassNotFoundException.class)
  public void test03() throws Exception {
    new Foo().load("jp.kusumotolab.kgenprog.CUILauncher");
  }

  // test03と全く同じ内容．記法を変えただけ．
  @Test
  public void test04() {
    try {
      new Foo().load("jp.kusumotolab.kgenprog.CUILauncher");
    } catch (ClassNotFoundException e) {
      assertTrue(true);
    }
  }

}
