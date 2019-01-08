package jp.kusumotolab.kgenprog.project;

public abstract class FullyQualifiedName {

  final public String value;

  protected FullyQualifiedName(final String value) {
    // TODO check validation
    this.value = value;
  }

  public String getPackageName() {
    final int lastIndexOf = value.lastIndexOf(".");
    if (lastIndexOf == -1) {
      return "";
    }
    return value.substring(0, lastIndexOf);
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
