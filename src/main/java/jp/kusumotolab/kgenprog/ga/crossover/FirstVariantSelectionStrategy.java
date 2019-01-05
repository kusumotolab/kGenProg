package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public interface FirstVariantSelectionStrategy {

  Variant exec(List<Variant> variants);
}
