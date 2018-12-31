package example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class FooTest {

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
    assertEquals(-1, new Foo().foo(0));
  }

  @Test
  public void test04() {
    assertEquals(0, new Foo().foo2());
  }
}
