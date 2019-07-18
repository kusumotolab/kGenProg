package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * 再利用するコードを検索するためのクエリ
 */
public class Query {

  private final List<Variable> variables;
  private final Scope scope;
  private final boolean canNormal;
  private final boolean canBreak;
  private final boolean canReturn;
  private final FullyQualifiedName returnFQN;
  private final boolean canContinue;

  /**
   * コンストラクタ
   *
   * @param variables 変数の一覧
   */
  public Query(final List<Variable> variables) {
    this(variables, new Scope(Type.PROJECT, null), true, true, true, null, true);
  }

  /**
   * コンストラクタ
   *
   * @param scope 再利用するノードのスコープ
   */
  public Query(final Scope scope) {
    this(Collections.emptyList(), scope, true, true, true, null, true);
  }

  /**
   * コンストラクタ
   *
   * @param variables 変数の一覧
   * @param scope 再利用するノードのスコープ
   * @param canBreak break 文を再利用可能か
   * @param canReturn return 文を再利用可能か
   * @param canContinue continue 文を再利用可能か
   */
  public Query(final List<Variable> variables, final Scope scope, final boolean canNormal, final boolean canBreak,
      final boolean canReturn, final FullyQualifiedName returnFQN, final boolean canContinue) {
    this.canNormal = canNormal;
    this.variables = variables;
    this.scope = scope;
    this.canBreak = canBreak;
    this.canReturn = canReturn;
    this.returnFQN = returnFQN;
    this.canContinue = canContinue;
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

  public boolean canNormal() {
    return canNormal;
  }

  public boolean canBreak() {
    return canBreak;
  }

  public boolean canReturn() {
    return canReturn;
  }

  public FullyQualifiedName getReturnFQN() {
    return returnFQN;
  }

  public boolean canContinue() {
    return canContinue;
  }
}
