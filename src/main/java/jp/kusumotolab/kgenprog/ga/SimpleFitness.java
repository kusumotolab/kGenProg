package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFitness implements Fitness {

  private static Logger log = LoggerFactory.getLogger(Fitness.class);

  final private double value;

  public SimpleFitness(double value) {
    this.value = value;
  }

  @Override
  public double getValue() {
    log.debug("enter getValue()");
    return value;
  }

}
