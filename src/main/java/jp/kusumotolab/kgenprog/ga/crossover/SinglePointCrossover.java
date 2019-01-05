package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

public class SinglePointCrossover extends CrossoverAdaptor {

  private final Random random;

  public SinglePointCrossover(final Random random,
      final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
      final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
      final int generatingCount) {
    super(firstVariantSelectionStrategy, secondVariantSelectionStrategy, generatingCount);
    this.random = random;
  }

  @Override
  public List<Variant> makeVariants(final List<Variant> variants, final VariantStore store) {
    final Variant variantA = getFirstVariantSelectionStrategy().exec(variants);
    final Variant variantB = getSecondVariantSelectionStrategy().exec(variants, variantA);
    final Gene geneA = variantA.getGene();
    final Gene geneB = variantB.getGene();
    final List<Base> basesA = geneA.getBases();
    final List<Base> basesB = geneB.getBases();

    final int index = getPointAtRandom(basesA.size(), basesB.size());
    final Gene newGeneA = makeGene(basesA.subList(0, index), basesB.subList(index, basesB.size()));
    final Gene newGeneB = makeGene(basesB.subList(0, index), basesA.subList(index, basesA.size()));
    final HistoricalElement elementA = new CrossoverHistoricalElement(variantA, variantB, index);
    final HistoricalElement elementB = new CrossoverHistoricalElement(variantB, variantA, index);
    return Arrays.asList(store.createVariant(newGeneA, elementA),
        store.createVariant(newGeneB, elementB));
  }

  private int getPointAtRandom(final int a, final int b) {
    // random.nextInt(a) は 0 ~ a の間の整数値（0は含むがaは含まない）をランダムで出力するので、
    // 0 を避けるために 1 足している
    final int min = Math.min(a, b);
    return random.nextInt(min - 1) + 1;
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {
    final List<Base> bases = Stream.concat(basesA.stream(), basesB.stream())
        .collect(Collectors.toList());
    return new Gene(bases);
  }
}
