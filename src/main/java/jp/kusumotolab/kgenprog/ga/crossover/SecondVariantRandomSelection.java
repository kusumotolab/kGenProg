package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class SecondVariantRandomSelection implements SecondVariantSelectionStrategy {

  private final Random random;

  public SecondVariantRandomSelection(final Random random) {
    this.random = random;
  }

  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant) {
    return variants.get(random.nextInt(variants.size()));
  }


}
