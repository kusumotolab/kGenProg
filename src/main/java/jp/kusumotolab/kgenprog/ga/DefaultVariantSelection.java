package jp.kusumotolab.kgenprog.ga;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultVariantSelection implements VariantSelection {

  final private int maxVariantsPerGeneration;

  public DefaultVariantSelection() {
    this.maxVariantsPerGeneration = 100;
  }

  public DefaultVariantSelection(int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  @Override
  public List<Variant> exec(List<Variant> variants) {
    final List<Variant> list = variants.stream()
        .sorted(Comparator.comparingDouble(e -> ((Variant) e).getFitness().getValue()).reversed())
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
    return list;
  }
}
