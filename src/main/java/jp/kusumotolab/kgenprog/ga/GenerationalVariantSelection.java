package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerationalVariantSelection extends DefaultVariantSelection {

  private static Logger log = LoggerFactory.getLogger(GenerationalVariantSelection.class);

  private final List<List<Variant>> generations;

  public GenerationalVariantSelection() {
    this(100);
  }

  public GenerationalVariantSelection(final int maxVariantPerGeneration) {
    super(maxVariantPerGeneration);
    this.generations = new ArrayList<>();
  }

  @Override
  public List<Variant> exec(final List<Variant> variants) {
    log.debug("enter exec(List<>)");

    final List<Variant> variantsForSelection = new ArrayList<>();

    if (!generations.isEmpty()) {
      // TODO #171 で導入するAPIを使うべき
      // 最後の世代のうち，Fitness が 1.0でないものを variantsForSelection に追加
      final List<Variant> lastGenerationVariants = generations.get(generations.size() - 1);
      variantsForSelection.addAll(lastGenerationVariants.stream()
          .filter(v -> 0 != Double.compare(v.getFitness()
              .getValue(), 1.0d))
          .collect(Collectors.toList()));
    }

    variantsForSelection.addAll(variants);
    generations.add(variants);

    return super.exec(variantsForSelection);
  }
}
