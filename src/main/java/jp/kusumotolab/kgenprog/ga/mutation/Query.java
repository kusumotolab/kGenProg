package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.Variable;

public class Query {

  private final List<Variable> variables;
  private final Scope scope;

  public Query(final List<Variable> variables) {
    this(variables, new Scope(Type.PROJECT, null));
  }

  public Query(final Scope scope) {
    this(Collections.emptyList(), scope);
  }

  public Query(final List<Variable> variables, final Scope scope) {
    this.variables = variables;
    this.scope = scope;
  }

  public List<Variable> getVariables() {
    return variables;
  }

  public Scope getScope() {
    return scope;
  }
}
