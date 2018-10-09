package jp.kusumotolab.kgenprog.ga;

import java.util.Objects;
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

  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final SimpleFitness simpleFitness = (SimpleFitness) o;
    return Objects.equals(value, simpleFitness.value);
  }

  @Override
  public int hashCode() {
    return 31 + Double.hashCode(value);
  }
}
