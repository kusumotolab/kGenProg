package jp.kusumotolab.kgenprog.ga.validation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 指定された回数(期間)の評価後に評価値を0にするクラス
 *
 */
public class LimitedNumberSimpleFitness extends SimpleFitness {

  private final AtomicInteger availableCapacity;

  /**
   * 
   * @param value 保持する評価値
   * @param capacity 与えられた評価値を保持する期間
   */
  public LimitedNumberSimpleFitness(final double value, final int capacity) {
    super(value);
    this.availableCapacity = new AtomicInteger(capacity);
  }

  /**
   * 
   * return 評価値，ただし，期限切れの場合は0
   */
  @Override
  public double getValue() {
    return isCapacityAvailable() ? super.getValue() : 0d;
  }

  /**
   * @return 最大値かどうか
   */
  @Override
  public boolean isMaximum() {
    return isCapacityAvailable() ? super.isMaximum() : false;
  }

  /**
   * 
   * @return 残りの期間
   */
  public int getCapacity() {
    return availableCapacity.get();
  }

  /*
   * 期間を1つ減らす
   * 
   * @return 減ったあとの期間
   */
  public int reduceCapacity() {
    return isCapacityAvailable() ? availableCapacity.decrementAndGet() : availableCapacity.get();
  }

  /**
   * 
   * @return 期間が残っているか
   */
  public boolean isCapacityAvailable() {
    return 0 < availableCapacity.get();
  }
}
