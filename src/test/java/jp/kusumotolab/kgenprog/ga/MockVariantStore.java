package jp.kusumotolab.kgenprog.ga;

import java.util.List;

class MockVariantStore extends VariantStore {

  private final List<Variant> currentVariants;
  
  @SuppressWarnings("deprecation")
  public MockVariantStore(final List<Variant> currentVariants) {
    super(null);
    this.currentVariants = currentVariants;
  }
  
  @Override
  public List<Variant> getCurrentVariants(){
    return currentVariants;
  }
  
  @Override
  public Variant createVariant(final Gene gene) {
    
    return new Variant(gene, null, null, null, null);
  }
}
