package example;

import org.junit.Test;

public class QuxTest {

  @Test(timeout = 100)
  public void test01() {
    Qux.qux(false);
  }

  @Test(timeout = 100)
  public void test02() {
    Qux.qux(true); // infinite loop occured with timeout.
  }

  @Test
  public void test03() {
    Qux.qux(true); // infinite loop occured without timeout.
  }
}
