package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public abstract class SecondVariantSimilarityBasedSelection
    implements SecondVariantSelectionStrategy {

  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant) {
    double minSimilarity = 1.0d;
    Variant secondVariant = firstVariant;

    for (final Variant variant : variants) {
      final double similarity = calculateSimilarity(firstVariant, variant);
      if (similarity < minSimilarity) {
        minSimilarity = similarity;
        secondVariant = variant;
      }
    }

    return secondVariant;
  }

  protected abstract double calculateSimilarity(final Variant variant1, final Variant variant2);
}
