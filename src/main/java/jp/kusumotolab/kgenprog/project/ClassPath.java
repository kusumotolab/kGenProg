package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;

public final class ClassPath {

  public final Path path;

  public ClassPath(final Path path) {
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
