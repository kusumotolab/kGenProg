package jp.kusumotolab.kgenprog.ga.validation;

/**
 * double で評価値を保持するクラス
 */
public class SimpleFitness implements Fitness {

  public static final double MAXIMUM_VALUE = 1.0d;
  private final double value;

  /**
   * @param value 保持する評価値
   */
  public SimpleFitness(double value) {
    this.value = value;
  }

  /**
   * @return 評価値
   */
  @Override
  public double getValue() {
    return value;
  }

  /**
   * @return 最大値かどうか
   */
  @Override
  public boolean isMaximum() {
    return 0 == Double.compare(value, MAXIMUM_VALUE);
  }
}
