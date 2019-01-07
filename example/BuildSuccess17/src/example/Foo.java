package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Foo {

  public String read(final Path path) {
    try {
      return Files.readAllLines(path).get(0);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return "";
  }
}
