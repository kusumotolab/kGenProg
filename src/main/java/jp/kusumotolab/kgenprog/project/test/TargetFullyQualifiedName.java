package jp.kusumotolab.kgenprog.project.test;

import java.nio.file.Path;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class TargetFullyQualifiedName extends FullyQualifiedName {

  private static final long serialVersionUID = 1L;

  public TargetFullyQualifiedName(final String value) {
    super(value);
  }

  @Deprecated
  public TargetFullyQualifiedName(final SourceFile sourceFile) {
    super(sourceFile);
  }

  @Deprecated
  public TargetFullyQualifiedName(final Path path) {
    super(path);
  }
}
