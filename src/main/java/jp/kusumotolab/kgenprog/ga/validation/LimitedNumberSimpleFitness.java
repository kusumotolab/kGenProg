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
    return isCapacityAvailable() ? super.getValue() : 0d;
  }

  @Override
  public boolean isMaximum() {
    return isCapacityAvailable() ? super.isMaximum() : false;
  }

  public int getCapacity() {
    return availableCapacity.get();
  }

  public int reduceCapacity() {
    return isCapacityAvailable() ? availableCapacity.decrementAndGet() : availableCapacity.get();
  }

  public boolean isCapacityAvailable() {
    return 0 < availableCapacity.get();
  }
}
