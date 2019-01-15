package example;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FooTest {

  @Test
  public void test01() throws Exception {
    final int size = new Foo().getBinarySizeFromClassLoader("example/Bar.class");
    assertTrue(size > 0);
  }

  @Test
  public void test02() throws Exception {
    final int size = new Foo().getBinarySizeFromClassLoader("example/NON-EXISTENT.class");
    assertTrue(size == 0);
  }
}
