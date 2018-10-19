package jp.kusumotolab.kgenprog.ga;

public class MetricFitness implements Fitness {

  private static double INITIAL_METRIC = -1;

  private final double metric;

  public MetricFitness(final double metric) {
    this.metric = metric;
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
    return metric < INITIAL_METRIC;
  }
}
