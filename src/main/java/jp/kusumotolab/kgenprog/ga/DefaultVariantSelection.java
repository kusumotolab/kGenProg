package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultVariantSelection implements VariantSelection {

  final private int maxVariantsPerGeneration;

  public DefaultVariantSelection() {
    this.maxVariantsPerGeneration = 100;
  }

  public DefaultVariantSelection(int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  @Override
  public List<Variant> exec(final List<Variant> current, final List<Variant> generated) {
    final List<Variant> list = Stream.concat(current.stream(), generated.stream())
        .sorted((o1, o2) -> compareFitness(o1.getFitness(), o2.getFitness()))
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
    return list;
  }

  private int compareFitness(final Fitness fitness1, final Fitness fitness2) {
    if (Double.isNaN(fitness1.getValue()) && Double.isNaN(fitness2.getValue())) {
      return 0;
    }
    if (Double.isNaN(fitness1.getValue())) {
      return 1;
    }
    if (Double.isNaN(fitness2.getValue())) {
      return -1;
    }
    return -Double.compare(fitness1.getValue(), fitness2.getValue());
  }
}
