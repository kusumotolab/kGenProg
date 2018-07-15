package jp.kusumotolab.kgenprog.project.test;

import java.io.Serializable;

public abstract class FullyQualifiedName implements Serializable {

  final public String value;

  protected FullyQualifiedName(final String value) {
    // TODO check validation
    this.value = value;
  }

  @Override
  public boolean equals(final Object o) {
    return this.toString()
        .equals(o.toString());
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
