package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinglePointCrossover implements Crossover {

  private static Logger log = LoggerFactory.getLogger(SinglePointCrossover.class);

  private final Random random;
  private final int crossoverGeneratingCount;

  public SinglePointCrossover(final Random random, final int crossoverGeneratingCount) {
    this.random = random;
    this.crossoverGeneratingCount = crossoverGeneratingCount;
  }

  @Override
  public List<Variant> exec(final VariantStore variantStore) {
    log.debug("enter exec(List<>)");

    final List<Variant> filteredVariants = variantStore.getCurrentVariants()
        .stream()
        .filter(e -> !e.getGene()
            .getBases()
            .isEmpty())
        .collect(Collectors.toList());

    if (filteredVariants.isEmpty()) {
      return Collections.emptyList();
    }

    final List<Variant> variants = new ArrayList<>();

    for (int i = 0; i < crossoverGeneratingCount/2; i++) {
      final List<Variant> newVariants = makeVariants(filteredVariants, variantStore);
      variants.addAll(newVariants);
    }
    if (crossoverGeneratingCount != 0 && crossoverGeneratingCount % 2 != 0) {
      final List<Variant> newVariants = makeVariants(filteredVariants, variantStore);
      if (!newVariants.isEmpty()) {
        variants.add(newVariants.get(0));
      }
    }
    return variants;
  }

  private List<Variant> makeVariants(final List<Variant> variants, final VariantStore store) {
    final Variant variantA = variants.get(random.nextInt(variants.size()));
    final Variant variantB = variants.get(random.nextInt(variants.size()));
    final Gene geneA = variantA.getGene();
    final Gene geneB = variantB.getGene();
    final List<Base> basesA = geneA.getBases();
    final List<Base> basesB = geneB.getBases();
    if (!canMakeVariant(basesA, basesB)) {
      return Collections.emptyList();
    }
    final int index = getPointAtRandom(basesA.size(), basesB.size());
    final Gene newGeneA = makeGene(basesA.subList(0, index), basesB.subList(index, basesB.size()));
    final Gene newGeneB = makeGene(basesB.subList(0, index), basesA.subList(index, basesA.size()));
    final HistoricalElement elementA = new CrossoverHistoricalElement(variantA, variantB, index);
    final HistoricalElement elementB = new CrossoverHistoricalElement(variantB, variantA, index);
    return Arrays.asList(store.createVariant(newGeneA, elementA),
        store.createVariant(newGeneB, elementB));
  }

  private boolean canMakeVariant(final List<Base> basesA, final List<Base> basesB) {
    final int sizeA = basesA.size();
    final int sizeB = basesB.size();
    return Math.min(sizeA, sizeB) > 2;
  }

  private int getPointAtRandom(final int a, final int b) {
    // random.nextInt(a) は 0 ~ a の間の値をランダムで出力するので、
    // 0 を避けるために 1 足している
    final int min = Math.min(a, b);
    return random.nextInt(min - 2) + 1;
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {
    final ArrayList<Base> bases = new ArrayList<>(basesA);
    bases.addAll(basesB);
    return new Gene(bases);
  }
}
