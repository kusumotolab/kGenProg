package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;

import jp.kusumotolab.kgenprog.ga.variant.Variant;

public interface FirstVariantSelectionStrategy {

  Variant exec(List<Variant> variants);

  public enum Strategy {
    Elite {
      @Override
      public FirstVariantSelectionStrategy initialize(final Random random) {
        return new FirstVariantEliteSelection(random);
      }
    },

    Random {
      @Override
      public FirstVariantSelectionStrategy initialize(final Random random) {
        return new FirstVariantRandomSelection(random);
      }
    };

    public abstract FirstVariantSelectionStrategy initialize(final Random random);
  }
}
