package jp.kusumotolab.kgenprog.ga.validation;

import static java.lang.Double.isNaN;

public interface Fitness extends Comparable<Fitness> {

  double getValue();

  boolean isMaximum();

  /**
   * Compares two {@code Fitness} objects. Basically, this comparison is performed in the same way
   * as the numerical comparison with the {@code double} values returned from {@link #getValue()}
   * method. However, the value {@code NaN} is regarded as less than any other value.
   *
   * @param anotherFitness the {@code Fitness} to be compared.
   * @return the value {@code 0} if {@code anotherDouble} is equal to this {@code Fitness}; a value
   * less than {@code 0} if this {@code Fitness} is less than {@code anotherFitness}; and a value
   * greater than {@code 0} if this {@code Fitness} is greater than {@code anotherDouble}.
   */
  @Override
  default int compareTo(final Fitness anotherFitness) {
    if (isNaN(getValue()) && isNaN(anotherFitness.getValue())) {
      return 0;
    }
    if (isNaN(getValue())) {
      return -1;
    }
    if (isNaN(anotherFitness.getValue())) {
      return 1;
    }
    return Double.compare(getValue(), anotherFitness.getValue());
  }
}
