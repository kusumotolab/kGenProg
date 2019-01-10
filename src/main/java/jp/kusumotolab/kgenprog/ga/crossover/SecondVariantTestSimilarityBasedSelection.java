package jp.kusumotolab.kgenprog.ga.crossover;

import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class SecondVariantTestSimilarityBasedSelection
    extends SecondVariantSimilarityBasedSelection {

  @Override
  protected double calculateSimilarity(final Variant variant1, final Variant variant2) {
    return SimilarityCalculator.exec(variant1, variant2, v -> v.getTestResults()
        .getFailedTestFQNs());
  }
}
