package example;

import org.junit.Test;

public class FooTest {

  // タイムアウトアノテーションは外しておく
  // @Rule public final Timeout globalTimeout = Timeout.seconds(10);

  @Test
  public void test01() {
    new Foo().foo(false); // 無限ループなし．成功する
  }

  @Test
  public void test02() {
    new Foo().foo(true); // 無限ループ発生
  }

  @Test
  public void test03() {
    new Foo().foo(true); // 無限ループ発生
  }

}
