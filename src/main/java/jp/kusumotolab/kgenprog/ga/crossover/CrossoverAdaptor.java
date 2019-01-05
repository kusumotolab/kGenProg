package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

public abstract class CrossoverAdaptor implements Crossover {

  private final FirstVariantSelectionStrategy firstVariantSelectionStrategy;
  private final SecondVariantSelectionStrategy secondVariantSelectionStrategy;
  private final int generatingCount;

  public CrossoverAdaptor(final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
      final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
      final int generatingCount) {
    this.firstVariantSelectionStrategy = firstVariantSelectionStrategy;
    this.secondVariantSelectionStrategy = secondVariantSelectionStrategy;
    this.generatingCount = generatingCount;
  }
  
  @Override
  public FirstVariantSelectionStrategy getFirstVariantSelectionStrategy() {
    return firstVariantSelectionStrategy;
  }

  @Override
  public SecondVariantSelectionStrategy getSecondVariantSelectionStrategy() {
    return secondVariantSelectionStrategy;
  }

  @Override
  final public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> filteredVariants = variantStore.getCurrentVariants()
        .stream()
        .filter(e -> 1 < e.getGene() // 遺伝子の長さが2に満たないバリアントは交叉に使えない
            .getBases()
            .size())
        .collect(Collectors.toList());

    // filteredVariantsの要素数が2に満たない場合は交叉しない
    if (filteredVariants.size() < 2) {
      return Collections.emptyList();
    }

    final List<Variant> variants = new ArrayList<>();

    // generatingCountを超えるまでバリアントを作りづづける
    while (variants.size() < generatingCount) {
      final List<Variant> newVariants = makeVariants(filteredVariants, variantStore);
      variants.addAll(newVariants);
    }

    // バリアントを作りすぎた場合はそれを除いてリターン
    return variants.subList(0, generatingCount);
  }

  protected abstract List<Variant> makeVariants(List<Variant> variants, VariantStore variantStore);
}
