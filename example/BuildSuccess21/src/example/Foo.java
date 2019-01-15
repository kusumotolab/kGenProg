package example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Foo {

  public List<String> readResource(final String filename) throws Exception {
    final URL url = getClass().getResource(filename);
    return readAllLines(url);
  }

  public List<String> readResourceWithStaticClass(final String filename) throws Exception {
    final URL url = Foo.class.getResource(filename);
    return readAllLines(url);
  }

  public List<String> readResourceWithThreadClassLoader(final String filename) throws Exception {
    final URL url = Thread.currentThread()
        .getContextClassLoader()
        .getResource(filename);
    return readAllLines(url);
  }

  public List<String> readResourceWithSelfClassLoader(final String filename) throws Exception {
    final URL url = getClass().getClassLoader()
        .getResource(filename);
    return readAllLines(url);
  }

  //////////

  public List<String> readResourceAsStream(String filename) throws Exception {
    final InputStream is = getClass().getResourceAsStream(filename);
    return readAllLines(is);
  }

  public List<String> readResourceAsStreamWithStaticClass(String filename) throws Exception {
    final InputStream is = Foo.class.getResourceAsStream(filename);
    return readAllLines(is);
  }

  public List<String> readResourceAsStreamWithThreadClassLoader(String filename) throws Exception {
    final InputStream is = Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream(filename);
    return readAllLines(is);
  }

  public List<String> readResourceAsStreamWithSelfClassLoader(String filename) throws Exception {
    final InputStream is = getClass().getClassLoader()
        .getResourceAsStream(filename);
    return readAllLines(is);
  }

  //////////

  private List<String> readAllLines(URL url) throws Exception {
    final Path path = Paths.get(url.toURI());
    return Files.readAllLines(path);
  }

  private List<String> readAllLines(InputStream is) throws Exception {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    final List<String> result = new ArrayList<>();
    for (String line; (line = reader.readLine()) != null;) {
      result.add(line);
    }
    return result;
  }
}
