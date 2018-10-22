package jp.kusumotolab.kgenprog.project.build;

import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class BinaryStoreKey {

  final private String key;

  public BinaryStoreKey(final String name, final String hash) {
    key = name + "#" + hash;
  }

  public BinaryStoreKey(final GeneratedAST<? extends SourcePath> ast) {
    this(ast.getPrimaryClassName(), ast.getMessageDigest());
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
