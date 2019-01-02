package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Arrays;
import java.util.List;

public class RandomCrossoverHistoricalElement implements HistoricalElement {

  private final Variant parentA;
  private final Variant parentB;

  public RandomCrossoverHistoricalElement(final Variant parentA, final Variant parentB) {
    this.parentA = parentA;
    this.parentB = parentB;
  }

  @Override
  public List<Variant> getParents() {
    return Arrays.asList(parentA, parentB);
  }

  @Override
  public String getOperationName() {
    return "random-crossover";
  }
}
