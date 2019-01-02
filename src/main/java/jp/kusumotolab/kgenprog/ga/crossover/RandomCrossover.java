package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.RandomCrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * ランダム交叉を行うクラス
 *
 */
public class RandomCrossover extends CrossoverAdaptor {

  private final Random random;

  public RandomCrossover(final Random random,
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

    final Gene newGene = makeGene(basesA, basesB);
    final HistoricalElement newElement = new RandomCrossoverHistoricalElement(variantA, variantB);
    return Arrays.asList(store.createVariant(newGene, newElement));
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {

    final List<Base> concatenatedBases = Stream.concat(basesA.stream(), basesB.stream())
        .collect(Collectors.toList());
    final List<Base> bases = new ArrayList<>();

    for (int i = 0; true; i++) {

      // basesAとbasesBの短い方よりもiが小さいときは，
      // Baseをランダムに1つ選択
      if (i < Math.min(basesA.size(), basesB.size())) {
        final int selectedIndex = random.nextInt(concatenatedBases.size());
        final Base selectedBase = concatenatedBases.remove(selectedIndex);
        bases.add(selectedBase);
        continue;
      }

      // basesAとbasesBの長い方よりもiが小さいときは，
      // Baseをランダムに1つ選択するかどうかランダムに決める
      if (i < Math.max(basesA.size(), basesB.size())) {
        if (random.nextBoolean()) {
          final int selectedIndex = random.nextInt(concatenatedBases.size());
          final Base selectedBase = concatenatedBases.remove(selectedIndex);
          bases.add(selectedBase);
        }
        continue;
      }

      // basesAとbasesBの長い方よりもiが大きくなってしまったらループを抜ける
      break;
    }

    return new Gene(bases);
  }
}
