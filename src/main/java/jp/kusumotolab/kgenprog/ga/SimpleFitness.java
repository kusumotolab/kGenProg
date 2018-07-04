package jp.kusumotolab.kgenprog.ga;

public class SimpleFitness implements Fitness {

  final private double value;

  public SimpleFitness(double value) {
    this.value = value;
  }

  @Override
  public double getValue() {
    return value;
  }

}
