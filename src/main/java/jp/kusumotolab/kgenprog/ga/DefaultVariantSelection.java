package jp.kusumotolab.kgenprog.ga;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DefaultVariantSelection implements VariantSelection {

  final private int maxVariantsPerGeneration;

  public DefaultVariantSelection(int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  @Override
  public List<Variant> exec(List<Variant> variants) {
    final Map<Variant, Integer> hashMap = new HashMap<>();
    for (int i = 0; i < variants.size(); i++) {
      hashMap.put(variants.get(i), i);
    }
    final List<Variant> list = hashMap.entrySet().stream()
        .sorted((o1, o2) ->
            o2.getKey().getFitness().getValue() - o1.getKey().getFitness().getValue() > 0 ? 1 : -1)
        .limit(maxVariantsPerGeneration)
        .sorted(Comparator.comparingInt(Map.Entry::getValue))
        .map(Entry::getKey)
        .collect(Collectors.toList());
    return list;
  }
}
