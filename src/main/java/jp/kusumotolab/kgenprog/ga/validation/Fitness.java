package jp.kusumotolab.kgenprog.ga.validation;

/**
 * 個体の評価値を表現するインターフェース
 */
public interface Fitness extends Comparable<Fitness> {

  /**
   * 個体の評価値を0〜1の範囲のdouble型で返す．
   *
   * The fitness value is returned as double in the range of 0 to 1.
   *
   * @return 個体の評価値
   */
  double getNormalizedValue();

  /**
   * @return その評価値が最大値かどうか
   */
  boolean isMaximum();

  /**
   * @return 評価値の文字列表現を返す．
   */
  String toString();
}
