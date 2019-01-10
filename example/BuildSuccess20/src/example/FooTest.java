package example;

import junit.framework.TestCase;

public class FooTest extends TestCase { // 継承ベース

  // @Test
  public void test01() {
    new Foo().foo(false); // 無限ループなし．成功する
  }

  // @Test
  public void test02() {
    new Foo().foo(true); // 無限ループ発生
  }

  // @Test
  public void test03() {
    new Foo().foo(true); // 無限ループ発生
  }

}
