package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import jp.kusumotolab.kgenprog.project.SourceFile;

public abstract class FullyQualifiedName implements Serializable {

  private static final long serialVersionUID = 1L;

  final public String value;

  protected FullyQualifiedName(final String value) {
    // final String v = value.substring(0, value.lastIndexOf('.'));
    // this.value = v.replaceAll("\\" + File.separator, ".");
    this.value = value;
  }

  @Deprecated
  protected FullyQualifiedName(SourceFile sourceFile) {
    this(sourceFile.toString().substring(0, sourceFile.toString().lastIndexOf('.')).replace("/",
        "."));
  }

  @Deprecated
  protected FullyQualifiedName(Path path) {
    final String v = path.toString().substring(0, path.toString().lastIndexOf('.'));
    this.value = v.replaceAll("\\" + File.separator, ".");
  }

  @Override
  public boolean equals(final Object o) {
    return this.toString().equals(o.toString());
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }

  @Override
  public String toString() {
    return this.value;
  }
}
