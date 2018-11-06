package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;

public abstract class SourcePath {

  public final Path path;

  protected SourcePath(final Path path) {
    this.path = path;
  }

  @Override
  public boolean equals(final Object o) {
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

  public abstract FullyQualifiedName createFullyQualifiedName(String className);
}
