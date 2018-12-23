package jp.kusumotolab.kgenprog.ga.mutation.selection;

import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class ReuseCandidate<T> {

  private final T value;
  private final String packageName;
  private final FullyQualifiedName fqn;

  public ReuseCandidate(final T value, final String packageName,
      final FullyQualifiedName fqn) {
    this.value = value;
    this.packageName = packageName;
    this.fqn = fqn;
  }

  public T getValue() {
    return value;
  }

  public String getPackageName() {
    return packageName;
  }

  public FullyQualifiedName getFqn() {
    return fqn;
  }
}
