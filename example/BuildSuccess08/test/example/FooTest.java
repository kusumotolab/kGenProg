package example;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class FooTest {
  @Test public void test01(){
    assertEquals(10,Foo.max(1, 10));
  }
  @Test public void test02(){
    assertEquals(10,Foo.max(10, 1));
  }
  @Test public void test03(){
    assertEquals(-1,Foo.max(-10, -1));
  }
  @Test public void test04(){
    assertEquals(-1,Foo.max(-1, -10));
  }
}
