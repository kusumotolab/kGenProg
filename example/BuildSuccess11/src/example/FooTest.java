package example;

import org.junit.Test;
import junit.framework.TestCase;

public class FooTest extends TestCase { // 継承ベースのJUnitテスト

  @Test
  public void test01() {
    assertEquals(9, new Foo().foo(10));
  }

  @Test
  public void test02() {
    assertEquals(99, new Foo().foo(100));
  }

  @Test
  public void test03() {
    assertEquals(0, new Foo().foo(0));
  }

  @Test
  public void test04() {
    assertTrue(true);
  }
}
