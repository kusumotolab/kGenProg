package jp.kusumotolab.kgenprog.ga.mutation.analyse;

import java.util.Objects;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;

/**
 * 変数をあらほすクラス
 */
public class Variable {
  private final String name;
  private final FullyQualifiedName fqn;
  private final boolean isFinal;

  /**
   * コンストラクタ
   * @param name 変数名
   * @param type 変数の型の文字列
   * @param isFinal final 修飾子がついているかどうか
   */
  public Variable(final String name, final String type, final boolean isFinal) {
    this(name, new TargetFullyQualifiedName(type), isFinal);
  }

  /**
   * コンストラクタ
   * @param name 変数名
   * @param fqn 変数の型
   * @param isFinal final 修飾子がついているかどうか
   */
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

  /**
   * @return 変数名
   */
  public String getName() {
    return name;
  }

  /**
   * @return 完全限定名
   */
  public FullyQualifiedName getFqn() {
    return fqn;
  }

  /**
   * @return final 修飾子がついているかどうか
   */
  public boolean isFinal() {
    return isFinal;
  }
}
