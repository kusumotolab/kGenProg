package jp.kusumotolab.kgenprog.ga;

import java.util.List;

public interface Crossover {

  public List<Variant> exec(VariantStore variantStore);
}
