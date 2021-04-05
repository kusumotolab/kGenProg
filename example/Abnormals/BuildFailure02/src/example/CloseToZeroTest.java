package example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CloseToZeroTest {
  @Test
  public void test01() {
    assertEquals(9, new CloseToZero().close_to_zero(10));
  }

  @Test
  public void test02() {
    assertEquals(99, new CloseToZero().close_to_zero(100));
  }

  @Test
  public void test03() {
    assertEquals(0, new CloseToZero().close_to_zero(0));
  }

  @Test
  public void test04() {
    assertEquals(-9, new CloseToZero().close_to_zero(-10));
  }
}
