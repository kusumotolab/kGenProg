package example;

import org.junit.Test;

public class FooTest {

  @Test(timeout = 100)
  public void test01() {
    Foo.foo(false);
  }

  @Test(timeout = 100)
  public void test02() {
    Foo.foo(true); // infinite loop occured with timeout.
  }

  @Test
  public void test03() {
    Foo.foo(true); // infinite loop occured without timeout.
  }
}
