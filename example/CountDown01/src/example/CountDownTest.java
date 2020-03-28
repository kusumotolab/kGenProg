package example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CountDownTest {

  @Test
  public void test01() {
    assertEquals(9, new CountDown().countDown(10));
  }

  @Test
  public void test02() {
    assertEquals(0, new CountDown().countDown(1));
  }

  @Test
  public void test03() {
    assertEquals(0, new CountDown().countDown(0));
  }
}
