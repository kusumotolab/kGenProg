package jp.kusumotolab.kgenprog.ga;

import java.util.List;

public interface VariantSelection {

  List<Variant> exec(List<Variant> current, List<Variant> generated);
}
