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
  public double getSingularValue() {
    return value;
  }

  /**
   * @return 最大値かどうか
   */
  @Override
  public boolean isMaximum() {
    return 0 == Double.compare(value, MAXIMUM_VALUE);
  }

  /**
   * @return 評価値の文字列表現を返す
   */
  @Override
  public String toString(){
    return Double.toString(value);
  }

  @Override
  public int compareTo(final Fitness anotherFitness) {

    if (SimpleFitness.class != anotherFitness.getClass()) {
      throw new IllegalStateException("anotherFitness must be an instance of SimpleFitness.");
    }

    final SimpleFitness anotherSimpleFitness = (SimpleFitness) anotherFitness;
    if (Double.isNaN(value) && Double.isNaN(anotherSimpleFitness.value)) {
      return 0;
    }
    if (Double.isNaN(value)) {
      return -1;
    }
    if (Double.isNaN(anotherSimpleFitness.value)) {
      return 1;
    }
    return Double.compare(value, anotherSimpleFitness.value);
  }
}
