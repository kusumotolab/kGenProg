package example;

import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FooTest{
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void test01() throws IOException {
    final String hello = "Hello World";
    final String fileName = "foo";
    final Path path = tempFolder.getRoot().toPath().resolve(fileName);
    final List<String> list = Arrays.asList(hello);

    final Foo foo = new Foo();
    foo.write(path, list);

    final String str = Files.readAllLines(path).get(0);
    assertEquals(str, hello);
  }
}