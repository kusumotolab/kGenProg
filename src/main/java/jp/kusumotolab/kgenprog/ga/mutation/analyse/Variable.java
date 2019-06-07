package jp.kusumotolab.kgenprog.ga.mutation.analyse;

import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class Variable {
  private final String name;
  private final FullyQualifiedName fqn;

  public Variable(final String name, final FullyQualifiedName fqn) {
    this.name = name;
    this.fqn = fqn;
  }

  public String getName() {
    return name;
  }

  public FullyQualifiedName getFqn() {
    return fqn;
  }
}
