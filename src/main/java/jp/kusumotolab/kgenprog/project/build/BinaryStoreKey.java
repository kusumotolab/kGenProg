package jp.kusumotolab.kgenprog.project.build;

public class BinaryStoreKey {

  final private String key;

  public BinaryStoreKey(final String fqn, final String hash) {
    key = fqn + "#" + hash;
  }

  @Deprecated
  public BinaryStoreKey(final String fqn) {
    key = fqn + "#" + "--------";
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
