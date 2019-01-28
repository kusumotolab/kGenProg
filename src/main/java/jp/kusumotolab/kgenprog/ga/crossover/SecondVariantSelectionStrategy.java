package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;

import jp.kusumotolab.kgenprog.ga.variant.Variant;

public interface SecondVariantSelectionStrategy {

  Variant exec(List<Variant> variants, Variant firstVariant);

  public enum Strategy {
    Elite {
      @Override
      public SecondVariantSelectionStrategy initialize(final Random random) {
        return new SecondVariantEliteSelection();
      }
    },

    GeneSimilarity {
      @Override
      public SecondVariantSelectionStrategy initialize(final Random random) {
        return new SecondVariantGeneSimilarityBasedSelection();
      }
    },

    Random {
      @Override
      public SecondVariantSelectionStrategy initialize(final java.util.Random random) {
        return new SecondVariantRandomSelection(random);
      }
    },

    TestSimilarity {
      @Override
      public SecondVariantSelectionStrategy initialize(final Random random) {
        return new SecondVariantTestSimilarityBasedSelection();
      }
    };

    public abstract SecondVariantSelectionStrategy initialize(final Random random);
  }
}
