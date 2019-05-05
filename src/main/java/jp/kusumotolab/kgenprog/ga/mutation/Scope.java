package jp.kusumotolab.kgenprog.ga.mutation;

import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * 　再利用するソースコードのスコープと起点となる FQN を表すクラス
 */
public class Scope {

  /**
   * スコープを表す enum
   */
  public enum Type {
    PROJECT, PACKAGE, FILE
  }

  private final Type type;
  private final FullyQualifiedName fqn;

  /**
   * コンストラクタ
   *
   * @param type 再利用するスコープ
   * @param fqn 再利用する起点となるソースコードの FQN
   */
  public Scope(final Type type, final FullyQualifiedName fqn) {
    this.type = type;
    this.fqn = fqn;
  }

  /**
   * @return 再利用するスコープ
   */
  public Type getType() {
    return type;
  }

  /**
   * @return 再利用する起点となるソースコードの FQN
   */
  public FullyQualifiedName getFqn() {
    return fqn;
  }
}
