package example;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class FooTest {

  @Test
  public void test01() {
    try {
      new Foo().foo();
      fail();
    } catch (ClassNotFoundException e) {
      assertTrue(true);
    }
  }

  @Test(expected = ClassNotFoundException.class)
  public void test02() throws Exception {
    new Foo().foo();
    fail();
  }

}
