package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * ソースコードの変異を行うクラス
 */
public abstract class Mutation {

  protected final Random random;
  protected final int mutationGeneratingCount;
  protected final CandidateSelection candidateSelection;
  protected final VariantCreator variantCreator;

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param noHistoryRecord 個体が生成される過程を記録するか否か
   */
  public Mutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final boolean noHistoryRecord) {
    this.random = random;
    this.mutationGeneratingCount = mutationGeneratingCount;
    this.candidateSelection = candidateSelection;
    this.variantCreator = newVariantCreator(noHistoryRecord);
  }

  /**
   * @param candidates 再利用するソースコード群
   */
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    candidateSelection.setCandidates(candidates);
  }

  /**
   * 変異処理された Variant を mutationGeneratingCount 分だけ返す
   *
   * @param variantStore Variant の情報を格納するオブジェクト
   * @return 変異された Gene を持った Variant のリスト
   */
  public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> generatedVariants = new ArrayList<>();

    final List<Variant> currentVariants = variantStore.getCurrentVariants();

    final Roulette<Variant> variantRoulette = new Roulette<>(currentVariants, e -> {
      final Fitness fitness = e.getFitness();
      final double value = fitness.getValue();
      return Double.isNaN(value) ? 0 : value;
    }, random);

    for (int i = 0; i < mutationGeneratingCount; i++) {
      final Variant variant = variantRoulette.exec();
      final List<Suspiciousness> suspiciousnesses = variant.getSuspiciousnesses();
      final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);

      if (suspiciousnesses.isEmpty()) {
        continue;
      }
      final Roulette<Suspiciousness> roulette =
          new Roulette<>(suspiciousnesses, weightFunction, random);

      final Suspiciousness suspiciousness = roulette.exec();
      final Base base = makeBase(suspiciousness);
      final Gene gene = makeGene(variant.getGene(), base);
      generatedVariants.add(variantCreator.createVariant(variantStore, variant, base, gene));
    }

    return generatedVariants;
  }

  protected Base makeBase(final Suspiciousness suspiciousness) {
    final ASTLocation location = suspiciousness.getLocation();
    return new Base(location, makeOperation(location));
  }

  protected abstract Operation makeOperation(final ASTLocation location);

  protected Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new Gene(bases);
  }

  private VariantCreator newVariantCreator(boolean noHistoryRecord) {
    if (noHistoryRecord) {
      return (variantStore, variant, base, gene) -> variantStore.createVariant(gene, null);
    } else {
      return (variantStore, variant, base, gene) -> {
        final HistoricalElement element = new MutationHistoricalElement(variant, base);
        return variantStore.createVariant(gene, element);
      };
    }
  }

  interface VariantCreator {

    Variant createVariant(VariantStore variantStore, Variant variant, Base base, Gene gene);
  }

}
