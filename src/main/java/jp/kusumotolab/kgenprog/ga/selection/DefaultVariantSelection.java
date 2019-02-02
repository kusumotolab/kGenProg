package jp.kusumotolab.kgenprog.ga.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class DefaultVariantSelection implements VariantSelection {

  final private int maxVariantsPerGeneration;

  public DefaultVariantSelection(final int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  @Override
  public List<Variant> exec(final List<Variant> current, final List<Variant> generated) {
    final ArrayList<Variant> variants = new ArrayList<>(current);
    variants.addAll(generated);
    Collections.shuffle(variants);
    final List<Variant> list = variants.stream()
        .sorted(Comparator.comparing(Variant::getFitness)
            .reversed())
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
    return list;
  }
}
