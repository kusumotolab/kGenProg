package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class TestSimilarityBasedUniformCrossover extends SimilarityBasedUniformCrossover {

  public TestSimilarityBasedUniformCrossover(final Random random,
      final int crossoverGeneratingCount) {
    super(random, crossoverGeneratingCount);
  }

  @Override
  protected double calculateSimilarity(final Variant variant1, final Variant variant2) {
    return SimilarityCalculator.exec(variant1, variant2, v -> v.getTestResults()
        .getFailedTestFQNs());
  }
}
