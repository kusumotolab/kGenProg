package jp.kusumotolab.kgenprog.ga;

public class SimpleFitness implements Fitness {

  public static double MAXIMUM_VALUE = 1.0d;
  final private double value;

  public SimpleFitness(double value) {
    this.value = value;
  }

  @Override
  public double getValue() {
    return value;
  }

  @Override
  public boolean isMaximum() {
    return 0 == Double.compare(value, MAXIMUM_VALUE);
  }
}
