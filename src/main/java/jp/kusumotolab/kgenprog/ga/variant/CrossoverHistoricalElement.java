package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Arrays;
import java.util.List;

public class CrossoverHistoricalElement implements HistoricalElement {

  private final Variant parentA;
  private final Variant parentB;
  private final int crossoverPoint;

  public CrossoverHistoricalElement(final Variant parentA, final Variant parentB,
      final int crossoverPoint) {
    this.parentA = parentA;
    this.parentB = parentB;
    this.crossoverPoint = crossoverPoint;
  }

  @Override
  public List<Variant> getParents() {
    return Arrays.asList(parentA, parentB);
  }

  @Override
  public String getOperationName() {
    return "crossover";
  }

  public int getCrossoverPoint() {
    return crossoverPoint;
  }
}
