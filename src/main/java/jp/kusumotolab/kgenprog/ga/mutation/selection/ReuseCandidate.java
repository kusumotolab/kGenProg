package jp.kusumotolab.kgenprog.ga.mutation.selection;

import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * 再利用する候補の情報を格納するクラス
 *
 * @param <T> 再利用するクラス
 */
public class ReuseCandidate<T> {

  private final T value;
  private final String packageName;
  private final FullyQualifiedName fqn;

  /**
   * コンストラクタ
   *
   * @param value 再利用するオブジェクト
   * @param packageName 再利用するオブジェクトのパッケージ
   * @param fqn 再利用するオブジェクトの FQN
   */
  public ReuseCandidate(final T value, final String packageName,
      final FullyQualifiedName fqn) {
    this.value = value;
    this.packageName = packageName;
    this.fqn = fqn;
  }

  /**
   * @return 再利用するオブジェクト
   */
  public T getValue() {
    return value;
  }

  /**
   * @return 再利用するオブジェクトのパッケージ
   */
  public String getPackageName() {
    return packageName;
  }

  /**
   * @return 再利用するオブジェクトの FQN
   */
  public FullyQualifiedName getFqn() {
    return fqn;
  }
}
