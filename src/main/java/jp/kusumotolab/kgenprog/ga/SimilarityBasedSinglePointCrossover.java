package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Random;

public class SimilarityBasedSinglePointCrossover extends SinglePointCrossover {

  public SimilarityBasedSinglePointCrossover(final Random random,
      final int crossoverGeneratingCount) {
    super(random, crossoverGeneratingCount);
  }

  @Override
  public Variant selectSecondVariant(final List<Variant> variants, final Variant firstVariant) {

    final Gene firstGene = firstVariant.getGene();
    double minSimilarity = 1.0d;
    Variant secondVariant = null;

    for (final Variant variant : variants) {
      final Gene gene = variant.getGene();
      final double similarity = Gene.getSimilarity(firstGene, gene);
      if (similarity < minSimilarity) {
        minSimilarity = similarity;
        secondVariant = variant;
      }
    }

    return secondVariant;
  }
}
