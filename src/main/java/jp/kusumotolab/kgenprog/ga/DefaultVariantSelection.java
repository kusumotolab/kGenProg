package jp.kusumotolab.kgenprog.ga;

import java.util.Comparator;
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
  public List<Variant> exec(List<Variant> variants) {
    log.debug("enter exec(List<>)");

    final List<Variant> list = variants.stream()
        .sorted(Comparator.<Variant>comparingDouble(e -> e.getFitness()
            .getValue())
            .reversed())
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
    return list;
  }
}
