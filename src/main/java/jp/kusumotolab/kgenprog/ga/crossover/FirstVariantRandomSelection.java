package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class FirstVariantRandomSelection implements FirstVariantSelectionStrategy {

  private final Random random;

  public FirstVariantRandomSelection(final Random random) {
    this.random = random;
  }

  @Override
  public Variant exec(final List<Variant> variants) {
    return variants.get(random.nextInt(variants.size()));
  }
}
