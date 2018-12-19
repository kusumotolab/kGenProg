package jp.kusumotolab.kgenprog.ga.selection;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public interface VariantSelection {

  List<Variant> exec(List<Variant> current, List<Variant> generated);
}
