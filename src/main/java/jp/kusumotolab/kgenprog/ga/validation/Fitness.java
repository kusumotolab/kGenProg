package jp.kusumotolab.kgenprog.ga.validation;

/**
 * 個体の評価値を表現するインターフェース
 */
public interface Fitness extends Comparable<Fitness> {

  /**
   * @return 個体の評価値を単一の数値で返す．
   */
  double getSingularValue();

  /**
   * @return その評価値が最大値かどうか
   */
  boolean isMaximum();

  /**
   * @return 評価値の文字列表現を返す．
   */
  String toString();
}
