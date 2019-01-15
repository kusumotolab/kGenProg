package example;

import java.io.InputStream;

public class Foo {

  public int getBinarySizeFromClassLoader(final String filename) throws Exception {
    final InputStream is = getClass().getClassLoader()
        .getResourceAsStream(filename);
    if (is == null) {
      return 0;
    }
    return is.available();
  }
}
