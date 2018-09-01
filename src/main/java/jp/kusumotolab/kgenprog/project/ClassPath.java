package jp.kusumotolab.kgenprog.project;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassPath {

  private static Logger log = LoggerFactory.getLogger(ClassPath.class);

  public final URL url;

  public ClassPath(final URL url) {
    log.debug("enter ClassPath(URL), {}", url.toString());
    this.url = url;
  }

  public ClassPath(final Path path) {
    log.debug("enter ClassPath(Path), {}", path.toString());
    this.url = convertToURL(path);
  }

  @Override
  public boolean equals(Object o) {
    return this.toString()
        .equals(o.toString());
  }

  @Override
  public int hashCode() {
    return url.hashCode();
  }

  @Override
  public String toString() {
    return this.url.toString();
  }

  private static URL convertToURL(final Path path) {
    URL url = null;
    try {
      final URI uri = path.toUri();
      url = uri.toURL();
    } catch (final MalformedURLException e) {
      log.debug("exit with MalformedURLException, {}", e.getMessage());
      System.exit(1);
    }
    return url;
  }
}
