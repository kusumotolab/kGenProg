package example;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class FooTest {

  @Test
  public void test01() throws IOException {
    // 一時ファイルへの書き込みをテストする
    final Path path = Files.createTempFile("kgp-", Long.toString(System.nanoTime()));

    final List<String> contents = Arrays.asList(Double.toString(Math.random()));

    new Foo().write(path, contents);

    final List<String> actualContents = Files.readAllLines(path);
    assertEquals(actualContents, contents);
  }
}
