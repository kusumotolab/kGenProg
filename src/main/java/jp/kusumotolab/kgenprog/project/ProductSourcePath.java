package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;

public final class ProductSourcePath extends SourcePath {

  public ProductSourcePath(final Path path) {
    super(path);
  }

  @Override
  public FullyQualifiedName createFullyQualifiedName(final String className) {
    return new TargetFullyQualifiedName(className);
  }
}
