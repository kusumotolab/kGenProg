package jp.kusumotolab.kgenprog.ga.validation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 指定された回数の評価後に評価値を0にするクラス
 */
public class LimitedNumberSimpleFitness extends SimpleFitness {

  private final AtomicInteger availableCapacity;

  /**
   * @param value 保持する評価値
   * @param capacity 与えられた評価値を保持する評価の回数
   */
  public LimitedNumberSimpleFitness(final double value, final int capacity) {
    super(value);
    this.availableCapacity = new AtomicInteger(capacity);
  }

  /**
   * 個体の評価値を0〜1の範囲のdouble型で返す．
   * ただし，期限切れの場合は0になる．
   *
   * The fitness value is returned as double in the range of 0 to 1.
   * In the case of expiration, 0 is returned.
   *
   * @return 個体の評価値
   */
  @Override
  public double getNormalizedValue() {
    return isCapacityAvailable() ? super.getNormalizedValue() : 0d;
  }

  /**
   * @return 最大値かどうか
   */
  @Override
  public boolean isMaximum() {
    return isCapacityAvailable() && super.isMaximum();
  }

  /**
   * @return 評価値の文字列表現を返す
   */
  @Override
  public String toString() {
    return Double.toString(getNormalizedValue());
  }

  /**
   * @return 与えられた評価値を保持する評価の残りの回数
   */
  public int getCapacity() {
    return availableCapacity.get();
  }

  /*
   * 評価値を保持する評価の回数を1つ減らす
   *
   * @return 減らしたあとの評価の残り回数
   */
  public int reduceCapacity() {
    return isCapacityAvailable() ? availableCapacity.decrementAndGet() : availableCapacity.get();
  }

  /**
   * @return 評価値を保持する評価の回数が残っているか
   */
  public boolean isCapacityAvailable() {
    return 0 < availableCapacity.get();
  }
}
