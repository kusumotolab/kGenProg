package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;

public class TestSourcePath extends SourcePath {

  public TestSourcePath(final Path path) {
    super(path);
  }

  @Override
  public FullyQualifiedName createFullyQualifiedName(final String className) {
    return new TestFullyQualifiedName(className);
  }
}
