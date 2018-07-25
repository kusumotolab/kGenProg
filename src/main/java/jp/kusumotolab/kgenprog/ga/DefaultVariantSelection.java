package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVariantSelection implements VariantSelection {

  private static Logger log = LoggerFactory.getLogger(DefaultCodeValidation.class);

  final private int maxVariantsPerGeneration;

  public DefaultVariantSelection() {
    this.maxVariantsPerGeneration = 100;
  }

  public DefaultVariantSelection(int maxVariantPerGeneration) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
  }

  @Override
  public List<Variant> exec(final List<Variant> variants) {
    log.debug("enter exec(List<>)");

    final List<Variant> list = variants.stream()
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
