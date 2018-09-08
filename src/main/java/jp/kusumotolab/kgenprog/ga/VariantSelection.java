package jp.kusumotolab.kgenprog.ga;

import java.util.List;

public interface VariantSelection {

  public List<Variant> exec(List<Variant> current, List<Variant> generated);
}
