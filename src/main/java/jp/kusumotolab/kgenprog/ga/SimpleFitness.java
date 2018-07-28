package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFitness implements Fitness {

  private static Logger log = LoggerFactory.getLogger(Fitness.class);

  public static double MAXIMUM_VALUE = 1.0d;
  final private double value;

  public SimpleFitness(double value) {
    this.value = value;
  }

  @Override
  public double getValue() {
    log.debug("enter getValue()");
    return value;
  }

  @Override
  public boolean isMaximum() {
    log.debug("enter isMaximum()");
    return 0 == Double.compare(value, MAXIMUM_VALUE);
  }
}
