package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassPath {

  private static Logger log = LoggerFactory.getLogger(ClassPath.class);

  public final Path path;

  public ClassPath(final Path path) {
    log.debug("enter ClassPath(Path=\"{}\")", path.toString());
    this.path = path;
  }

  @Override
  public boolean equals(Object o) {
    return this.toString()
        .equals(o.toString());
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return this.path.toString();
  }
}
