package example;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class FooTest {

  @Test
  public void test01() throws Exception {
    final List<String> contents = new Foo().readResource("/test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

  @Test
  public void test02() throws Exception {
    final List<String> contents = new Foo().readResourceWithStaticClass("/test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

  @Test
  public void test03() throws Exception {
    final List<String> contents = new Foo().readResourceWithThreadClassLoader("test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

  @Test
  public void test04() throws Exception {
    final List<String> contents = new Foo().readResourceWithSelfClassLoader("test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

  //////////

  @Test
  public void test11() throws Exception {
    final List<String> contents = new Foo().readResourceAsStream("/test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

  @Test
  public void test12() throws Exception {
    final List<String> contents = new Foo().readResourceAsStreamWithStaticClass("/test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

  @Test
  public void test13() throws Exception {
    final List<String> contents = new Foo().readResourceAsStreamWithThreadClassLoader("test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

  @Test
  public void test14() throws Exception {
    final List<String> contents = new Foo().readResourceAsStreamWithSelfClassLoader("test.txt");
    assertEquals(contents, Arrays.asList("this is", "a", "resource file"));
  }

}
