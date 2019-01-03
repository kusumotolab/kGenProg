package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.UniformCrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 一様交叉を行うクラス
 *
 */
public class UniformCrossover extends CrossoverAdaptor {

  private final Random random;

  public UniformCrossover(final Random random,
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
    final HistoricalElement newElement = new UniformCrossoverHistoricalElement(variantA, variantB);
    return Arrays.asList(store.createVariant(newGene, newElement));
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {
    final List<Base> bases = new ArrayList<>();
    for (int i = 0; i < Math.max(basesA.size(), basesB.size()); i++) {

      // iがbasesAのサイズよりも大きい場合は，
      // basesBからBaseを取得するかどうかをランダムで決める
      if (basesA.size() <= i) {
        if (this.random.nextBoolean()) {
          bases.add(basesB.get(i));
        }
        continue;
      }

      // iがbasesBのサイズよりも大きい場合は，
      // basesAからBaseを取得するかどうかをランダムで決める
      if (basesB.size() <= i) {
        if (this.random.nextBoolean()) {
          bases.add(basesA.get(i));
        }
        continue;
      }

      // iがbaseAとbaseBのどちらのサイズよりも小さい場合は，
      // どちらのBaseを取得するかランダムに決める
      bases.add(this.random.nextBoolean() ? basesA.get(i) : basesB.get(i));
    }
    return new Gene(bases);
  }
}
