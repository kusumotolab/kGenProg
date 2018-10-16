package jp.kusumotolab.kgenprog.project.build;

import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class BinaryStoreKey {

  final private String key;

  public BinaryStoreKey(final String name, final String hash) {
    key = name + "#" + hash;
  }

  public BinaryStoreKey(final GeneratedAST ast) {
    this(ast.getPrimaryClassName(), ast.getMessageDigest());
  }

  public BinaryStoreKey(final TestSourcePath testPath) {
    this(testPath.path.toString(), "----");
  }

  @Deprecated
  public BinaryStoreKey(final String name) {
    this(name, "----");
  }

  @Override
  public boolean equals(Object o) {
    return key.equals((String) o);
  }

  @Override
  public String toString() {
    return key;
  }
}
