package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CascadeCrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 直列的な交叉を行うクラス．
 *
 * @author shinsuke
 */
public class CascadeCrossover extends CrossoverAdaptor {

  /**
   * @param firstStrategy 1つ目の親を選ぶためのアルゴリズム
   * @param secondStrategy 2つ目の親を選ぶためのアルゴリズム
   * @param requiredSolutions 生成する必要がある修正プログラムの数
   *
   */
  public CascadeCrossover(final FirstVariantSelectionStrategy firstStrategy,
      final SecondVariantSelectionStrategy secondStrategy, final int requiredSolutions) {
    super(firstStrategy, secondStrategy, 2, requiredSolutions);
  }


  @Override
  protected List<Variant> makeVariants(final List<Variant> variants, final VariantStore store)
      throws CrossoverInfeasibleException {
    final Variant v1 = getFirstVariantSelectionStrategy().exec(variants);
    final Variant v2 = getSecondVariantSelectionStrategy().exec(variants, v1);
    final HistoricalElement histElement = new CascadeCrossoverHistoricalElement(v1, v2);

    // create two variants from cascaded genes
    final Gene cascadeGene1 = createCascadeGene(v1, v2);
    final Gene cascadeGene2 = createCascadeGene(v2, v1); // NOSONAR: it's intentional.
    final Variant newVariant1 = store.createVariant(cascadeGene1, histElement);
    final Variant newVariant2 = store.createVariant(cascadeGene2, histElement);

    return Arrays.asList(newVariant1, newVariant2);
  }

  @Override
  protected List<Variant> filter(final List<Variant> variants) {
    return variants; // no filter necessary
  }

  private Gene createCascadeGene(final Variant v1, final Variant v2) {
    final List<Base> cascadeBases = Stream.of(v1, v2)
        .map(Variant::getGene)
        .map(Gene::getBases)
        .flatMap(Collection::stream)
        .distinct() // remove shared genes meaning these two variants have blood relation
        .collect(Collectors.toList());
    return new Gene(cascadeBases);
  }

}
