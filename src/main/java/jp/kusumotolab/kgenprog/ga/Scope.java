package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class Scope {

  public enum Type {
    PROJECT, PACKAGE, FILE
  }

  private final Type type;
  private final FullyQualifiedName fqn;

  public Scope(final Type type, final FullyQualifiedName fqn) {
    this.type = type;
    this.fqn = fqn;
  }

  public Type getType() {
    return type;
  }

  public FullyQualifiedName getFqn() {
    return fqn;
  }
}
