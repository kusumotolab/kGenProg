package example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GreatestCommonDividerTest {

  @Test
  public void test01() {
    assertEquals(1, new GreatestCommonDivider().gcd(1, 1));
  }

  @Test
  public void test02() {
    assertEquals(2, new GreatestCommonDivider().gcd(4, 2));
  }

  @Test
  public void test03() {
    assertEquals(1, new GreatestCommonDivider().gcd(0, 1));
  }
}
