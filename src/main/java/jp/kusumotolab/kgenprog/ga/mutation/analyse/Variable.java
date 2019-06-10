package jp.kusumotolab.kgenprog.ga.mutation.analyse;

import java.util.Objects;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;

public class Variable {
  private final String name;
  private final FullyQualifiedName fqn;
  private final boolean isFinal;

  public Variable(final String name, final String type, final boolean isFinal) {
    this(name, new TargetFullyQualifiedName(type), isFinal);
  }

  public Variable(final String name, final FullyQualifiedName fqn, final boolean isFinal) {
    this.name = name;
    this.fqn = fqn;
    this.isFinal = isFinal;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Variable variable = (Variable) o;
    return isFinal == variable.isFinal && name.equals(variable.name) && fqn.equals(variable.fqn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, fqn, isFinal);
  }

  public String getName() {
    return name;
  }

  public FullyQualifiedName getFqn() {
    return fqn;
  }

  public boolean isFinal() {
    return isFinal;
  }
}
