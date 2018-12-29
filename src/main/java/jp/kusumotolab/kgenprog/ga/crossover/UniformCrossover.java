package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
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
public class UniformCrossover implements Crossover {

  private final Random random;
  private final int crossoverGeneratingCount;

  public UniformCrossover(final Random random, final int crossoverGeneraingCount) {
    this.random = random;
    this.crossoverGeneratingCount = crossoverGeneraingCount;
  }

  @Override
  public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> filteredVariants = variantStore.getCurrentVariants()
        .stream()
        .filter(e -> !e.getGene()
            .getBases()
            .isEmpty())
        .collect(Collectors.toList());

    // filteredVariantsの要素数が2に満たない場合は交叉しない
    if (filteredVariants.size() < 2) {
      return Collections.emptyList();
    }

    final List<Variant> variants = new ArrayList<>();

    // crossoverGenetingCountを超えるまでバリアントを作りづづける
    while (variants.size() < crossoverGeneratingCount) {
      final List<Variant> newVariants = makeVariants(filteredVariants, variantStore);
      variants.addAll(newVariants);
    }

    // バリアントを作りすぎた場合はそれを除いてリターン
    return variants.subList(0, crossoverGeneratingCount);
  }

  private List<Variant> makeVariants(final List<Variant> variants, final VariantStore store) {
    final Variant variantA = selectFirstVariant(variants);
    final Variant variantB = selectSecondVariant(variants, variantA);
    final Gene geneA = variantA.getGene();
    final Gene geneB = variantB.getGene();
    final List<Base> basesA = geneA.getBases();
    final List<Base> basesB = geneB.getBases();
    if (!canMakeVariant(basesA, basesB)) {
      return Collections.emptyList();
    }

    final Gene newGene = makeGene(basesA, basesB);
    final HistoricalElement newElement = new UniformCrossoverHistoricalElement(variantA, variantB);
    return Arrays.asList(store.createVariant(newGene, newElement));
  }

  /**
   * 一つ目のバリアントを選ぶためのメソッド．
   * 
   * @param variants
   * @return
   */
  protected Variant selectFirstVariant(final List<Variant> variants) {
    return variants.get(random.nextInt(variants.size()));
  }

  /**
   * 二つ目のバリアントを選ぶためのメソッド． 一つ目に選んだバリアントの情報を利用できるように，引数で受け取る．
   * 
   * @param variants
   * @return
   */
  protected Variant selectSecondVariant(final List<Variant> variants, final Variant firstVariant) {
    return variants.get(random.nextInt(variants.size()));
  }

  private boolean canMakeVariant(final List<Base> basesA, final List<Base> basesB) {
    final int sizeA = basesA.size();
    final int sizeB = basesB.size();
    return Math.min(sizeA, sizeB) > 2;
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {
    final List<Base> bases = new ArrayList<>();
    for (int i = 0; i < Math.max(basesA.size(), basesB.size()); i++) {

      if (basesA.size() <= i) {
        if (this.random.nextBoolean()) {
          bases.add(basesB.get(i));
        }
        continue;
      }

      if (basesB.size() <= i) {
        if (this.random.nextBoolean()) {
          bases.add(basesA.get(i));
        }
        continue;
      }

      bases.add(this.random.nextBoolean() ? basesA.get(i) : basesB.get(i));
    }
    return new Gene(bases);
  }
}
