package jp.kusumotolab.kgenprog.ga.variant;

import java.util.List;

public class MockVariantStore extends VariantStore {

  private final List<Variant> currentVariants;

  @SuppressWarnings("deprecation")
  public MockVariantStore(final List<Variant> currentVariants) {
    super(null);
    this.currentVariants = currentVariants;
  }

  @Override
  public List<Variant> getCurrentVariants() {
    return currentVariants;
  }

  @Override
  public Variant createVariant(final Gene gene, final HistoricalElement element) {
    return new Variant(0, 0, gene, null, null, null, null, element);
  }
}
