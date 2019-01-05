package example;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class FooTest{

  @Test
  public void test01() throws IOException {
    final Path outPath = Paths.get("output/out.txt");
    final List<String> contents = Arrays.asList(Double.toString(Math.random()));

    new Foo().write(outPath, contents);

    final List<String> actualContents = Files.readAllLines(outPath);
    assertEquals(actualContents, contents);
  }
}
