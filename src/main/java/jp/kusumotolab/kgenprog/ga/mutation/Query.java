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
  private final boolean canReuseNonControlStatement;
  private final boolean canReuseBreakStatement;
  private final boolean canReuseReturnStatement;
  private final FullyQualifiedName returnFQN;
  private final boolean canReuseContinueStatement;
  private final boolean canReuseCondition;

  /**
   * コンストラクタ
   *
   * @param variables 変数の一覧
   */
  public Query(final List<Variable> variables) {
    this(variables, new Scope(Type.PROJECT, null), true, true, true, null, true, true);
  }

  /**
   * コンストラクタ
   *
   * @param scope 再利用するノードのスコープ
   */
  public Query(final Scope scope) {
    this(Collections.emptyList(), scope, true, true, true, null, true, true);
  }

  /**
   * コンストラクタ
   *
   * @param variables 変数の一覧
   * @param scope 再利用するノードのスコープ
   * @param canReuseBreakStatement break 文を再利用可能か
   * @param canReuseReturnStatement return 文を再利用可能か
   * @param canReuseContinueStatement continue 文を再利用可能か
   */
  public Query(final List<Variable> variables, final Scope scope,
      final boolean canReuseNonControlStatement, final boolean canReuseBreakStatement,
      final boolean canReuseReturnStatement, final FullyQualifiedName returnFQN,
      final boolean canReuseContinueStatement, final boolean canReuseCondition) {
    this.variables = variables;
    this.scope = scope;
    this.canReuseNonControlStatement = canReuseNonControlStatement;
    this.canReuseBreakStatement = canReuseBreakStatement;
    this.canReuseReturnStatement = canReuseReturnStatement;
    this.returnFQN = returnFQN;
    this.canReuseContinueStatement = canReuseContinueStatement;
    this.canReuseCondition = canReuseCondition;
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

  public boolean canReuseNonControlStatement() {
    return canReuseNonControlStatement;
  }

  public boolean canReuseBreakStatement() {
    return canReuseBreakStatement;
  }

  public boolean canReuseReturnStatement() {
    return canReuseReturnStatement;
  }

  public FullyQualifiedName getReturnFQN() {
    return returnFQN;
  }

  public boolean canReuseContinueStatement() {
    return canReuseContinueStatement;
  }

  /**
   * Check whether conditions of conditional blocks can be reused.
   * 条件式が再利用できるかどうか．
   *
   * @return 条件式が再利用できる場合は true
   */
  public boolean canReuseCondition() {
    return canReuseCondition;
  }
}
