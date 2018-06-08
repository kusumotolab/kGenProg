package jp.kusumotolab.kgenprog.project.test;

import java.nio.file.Path;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class TestFullyQualifiedName extends FullyQualifiedName {

  private static final long serialVersionUID = 1L;

  public TestFullyQualifiedName(final String value) {
    super(value);
  }

  @Deprecated
  public TestFullyQualifiedName(final SourceFile sourceFile) {
    super(sourceFile);
  }

  @Deprecated
  public TestFullyQualifiedName(final Path path) {
    super(path);
  }
}
