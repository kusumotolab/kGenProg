package example;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class BarTest {
  @Test public void test01(){
    assertEquals(11,Bar.bar1(10));
  }
  @Test public void test02(){
    assertEquals(101,Bar.bar1(100));
  }
  @Test public void test03(){
    assertEquals(9,Bar.bar2(10));
  }
  @Test public void test04(){
    assertEquals(-1,Bar.bar2(0));
  }
  @Test public void test05(){
    Bar.bar3();
    assertTrue(true);
  }
}
