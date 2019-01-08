package jp.kusumotolab.kgenprog.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClassPath {

  public final Path path;

  public ClassPath(final Path path) {
    this.path = path;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final ClassPath that = (ClassPath) o;
    try {
      return Files.isSameFile(path, that.path);
    } catch (final IOException e) {
      return false;
    }
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
