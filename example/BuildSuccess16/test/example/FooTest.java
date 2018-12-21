package example;

import static org.junit.Assert.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class FooTest{
  @Test
  public void test01() {
    final Foo foo = new Foo();
    final Path path = Paths.get("./example/BuildSuccess15/resources/test.txt");
    assertEquals(foo.readFile(path), "Hello World");
  }
}