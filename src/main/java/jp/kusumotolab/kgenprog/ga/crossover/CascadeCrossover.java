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
import jp.kusumotolab.kgenprog.project.jdt.JDTASTCrossoverLocation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

/**
 * 直列的な交叉を行うクラス．
 *
 * @author shinsuke
 */
public class CascadeCrossover extends CrossoverAdaptor {

  /**
   * @param firstStrategy 1つ目の親を選ぶためのアルゴリズム
   * @param secondStrategy 2つ目の親を選ぶためのアルゴリズム
   * @return 交叉を行うインスタンス
   */
  public CascadeCrossover(final FirstVariantSelectionStrategy firstStrategy,
      final SecondVariantSelectionStrategy secondStrategy) {
    super(firstStrategy, secondStrategy, 2);
  }

  @Override
  protected List<Variant> makeVariants(final List<Variant> variants, final VariantStore store)
      throws CrossoverInfeasibleException {
    final Variant v1 = getFirstVariantSelectionStrategy().exec(variants);
    final Variant v2 = getSecondVariantSelectionStrategy().exec(variants, v1);
    final HistoricalElement histElement = new CascadeCrossoverHistoricalElement(v1, v2);

    // create two variants from cascaded genes
    final Gene cascadeGene1 = createCascadeGene(v1, v2);
    final Gene cascadeGene2 = createCascadeGene(v2, v1); //NOSONAR: it's intentional.
    final Variant newVariant1 = store.createVariant(cascadeGene1, histElement);
    final Variant newVariant2 = store.createVariant(cascadeGene2, histElement);

    return Arrays.asList(newVariant1, newVariant2);
  }

  private Gene createCascadeGene(final Variant v1, final Variant v2) {
    final List<Base> cascadeBases = Stream.of(v1, v2)
        .map(Variant::getGene)
        .map(Gene::getBases)
        .flatMap(Collection::stream)
        .distinct() // remove shared genes which means these two variants have blood relation
        .map(this::mapBaseLocationToCrossoverLocation)
        .collect(Collectors.toList());
    return new Gene(cascadeBases);
  }

  private Base mapBaseLocationToCrossoverLocation(final Base base) {
    final JDTASTLocation loc = new JDTASTCrossoverLocation(
        (JDTASTLocation) base.getTargetLocation());
    return new Base(loc, base.getOperation());
  }
}
