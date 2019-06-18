package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.analyse.Variable;

/**
 * 再利用するコードを検索するためのクエリ
 */
public class Query {

  private final List<Variable> variables;
  private final Scope scope;

  /**
   * コンストラクタ
   * @param variables 変数の一覧
   */
  public Query(final List<Variable> variables) {
    this(variables, new Scope(Type.PROJECT, null));
  }

  /**
   * コンストラクタ
   * @param scope 再利用するノードのスコープ
   */
  public Query(final Scope scope) {
    this(Collections.emptyList(), scope);
  }

  /**
   * コンストラクタ
   * @param variables 変数の一覧
   * @param scope 再利用するノードのスコープ
   */
  public Query(final List<Variable> variables, final Scope scope) {
    this.variables = variables;
    this.scope = scope;
  }

  /**
   * @return 変数のの一覧
   */
  public List<Variable> getVariables() {
    return variables;
  }

  /**
   * @return 再利用するノードのスコープ
   */
  public Scope getScope() {
    return scope;
  }
}
