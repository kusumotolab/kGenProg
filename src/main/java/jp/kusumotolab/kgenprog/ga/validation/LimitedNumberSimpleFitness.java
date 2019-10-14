package jp.kusumotolab.kgenprog.ga.validation;

import java.util.concurrent.atomic.AtomicInteger;

public class LimitedNumberSimpleFitness extends SimpleFitness {

  private final AtomicInteger availableCapacity;

  public LimitedNumberSimpleFitness(final double value, final int capacity) {
    super(value);
    this.availableCapacity = new AtomicInteger(capacity);
  }

  @Override
  public double getValue() {
    return isCapacityAvailable() ? 0d : super.getValue();
  }

  int reduceCapacity() {
    return availableCapacity.decrementAndGet();
  }

  public boolean isCapacityAvailable() {
    return availableCapacity.get() <= 0;
  }
}
