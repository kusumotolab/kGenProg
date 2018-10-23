package jp.kusumotolab.kgenprog.ga;

public class MetricFitness implements Fitness {

  private static double INITIAL_METRIC = -1;

  private final double metric;
  private final double testSuccessRate;

  public MetricFitness(final double metric, double testSuccessRate) {
    this.metric = metric;
    this.testSuccessRate = testSuccessRate;
  }

  public static void init(final double initialMetric) {
    if (initialMetric == -1) {
      throw new UnsupportedOperationException();
    }

    INITIAL_METRIC = initialMetric;
  }

  @Override
  public double getValue() {
    return metric;
  }

  @Override
  public boolean isMaximum() {
    return testSuccessRate == 1.0 && metric < INITIAL_METRIC;
  }
}
