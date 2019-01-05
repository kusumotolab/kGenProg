package example;

import static org.junit.Assert.assertEquals;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class FooTest{

  @Test
  public void test01() {
    final Foo foo = new Foo();
    final Path path = Paths.get("resources/in.txt");
    assertEquals(foo.read(path), "Hello World");
  }
}
