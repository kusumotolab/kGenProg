package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

public interface Crossover {

  public List<Variant> exec(VariantStore variantStore);
}
