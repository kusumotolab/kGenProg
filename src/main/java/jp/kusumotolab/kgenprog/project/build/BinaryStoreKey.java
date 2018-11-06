package jp.kusumotolab.kgenprog.project.build;

import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

@Deprecated // TODO 一旦Depreしておく．改善の予定
public class BinaryStoreKey {

  final private String key;

  private BinaryStoreKey(final String name, final String digest) {
    key = name + "#" + digest;
  }

  public BinaryStoreKey(final GeneratedAST<? extends SourcePath> ast) {
    this(ast.getPrimaryClassName(), ast.getMessageDigest());
  }

  public BinaryStoreKey(final FullyQualifiedName originFqn, final String digest) {
    this(originFqn.value, digest);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    return key.equals(o.toString());
  }

  @Override
  public String toString() {
    return key;
  }
}
