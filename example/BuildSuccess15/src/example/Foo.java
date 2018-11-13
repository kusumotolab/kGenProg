package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Foo {

  public String readFile(final Path path) {
    String result = "";
    try {
      result = Files.readAllLines(path).get(0);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return result;
  }
}
