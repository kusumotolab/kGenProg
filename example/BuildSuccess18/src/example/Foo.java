package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Foo {

  public void write(final Path path, final List<String> list) {
    try {
      Files.write(path, list);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
