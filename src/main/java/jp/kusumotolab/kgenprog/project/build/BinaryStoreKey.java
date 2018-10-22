package jp.kusumotolab.kgenprog.project.build;

import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class BinaryStoreKey {

  final private String key;

  public BinaryStoreKey(final String name, final String hash) {
    key = name + "#" + hash;
  }

  public BinaryStoreKey(final GeneratedAST<? extends SourcePath> ast) {
    this(ast.getPrimaryClassName(), ast.getMessageDigest());
  }

  @Deprecated
  public BinaryStoreKey(final TestSourcePath testPath) {
    this(testPath.path.toString(), "----");
  }

  @Deprecated
  public BinaryStoreKey(final String name) {
    this(name, "----");
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return key.equals(o.toString());
  }

  @Override
  public String toString() {
    return key;
  }
}
