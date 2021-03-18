package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.HeuristicStatementSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementAndConditionSelection;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.tongfei.progressbar.*;

/**
 * ソースコードの変異を行うクラス
 */
public abstract class Mutation {

  protected final Random random;
  protected final int mutationGeneratingCount;
  protected final CandidateSelection candidateSelection;
  protected final int requiredSolutions;
  final Logger logger = LoggerFactory.getLogger("Test");

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param requiredSolutions 生成する必要がある修正プログラムの数
   */
  public Mutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final int requiredSolutions) {
    this.random = random;
    this.mutationGeneratingCount = mutationGeneratingCount;
    this.candidateSelection = candidateSelection;
    this.requiredSolutions = requiredSolutions;
  }

  /**
   * @param candidates 再利用するソースコード群
   */
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    candidateSelection.setCandidates(candidates);
  }

  /**
   * @param variant 再利用するソースコード群を含初期バリアント
   */
  public void setInitialCandidates(final Variant variant) {
    candidateSelection.setCandidates(variant.getGeneratedSourceCode()
        .getProductAsts());
  }

  /**
   * 変異処理された Variant を mutationGeneratingCount 分だけ返す
   *
   * @param variantStore Variant の情報を格納するオブジェクト
   * @return 変異された Gene を持った Variant のリスト
   */
  public List<Variant> exec(final VariantStore variantStore) {

    int foundSolutions = variantStore.getFoundSolutionsNumber()
        .get();

    // すでに必要な数の修正プログラムがある場合は何もせずにこのメソッドを抜ける
    if (requiredSolutions <= foundSolutions) {
      return Collections.emptyList();
    }

    final List<Variant> generatedVariants = new ArrayList<>();

    final List<Variant> currentVariants = variantStore.getCurrentVariants();

    final Roulette<Variant> variantRoulette = new Roulette<>(currentVariants, e -> {
      final Fitness fitness = e.getFitness();
      final double value = fitness.getNormalizedValue();
      return Double.isNaN(value) ? 0 : value;
    }, random);

    try (ProgressBar pb = new ProgressBarBuilder()
        .setInitialMax(mutationGeneratingCount)
        .setStyle(ProgressBarStyle.UNICODE_BLOCK)
        .setTaskName("Test")
//        .setMaxRenderedLength(50)
        .setConsumer(new DelegatingProgressBarConsumer(logger::info, 50))
        .build()) {
      // your taskName here

      for (int i = 0; i < mutationGeneratingCount; i++) {
        final Variant variant = variantRoulette.exec();
        final List<Suspiciousness> suspiciousnesses = variant.getSuspiciousnesses();
        final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(),
            2);

        if (suspiciousnesses.isEmpty()) {
          pb.step();
          continue;
        }
        final Roulette<Suspiciousness> roulette =
            new Roulette<>(suspiciousnesses, weightFunction, random);

        final Suspiciousness suspiciousness = roulette.exec();
        final Base base = makeBase(suspiciousness);
        final Gene gene = makeGene(variant.getGene(), base);
        final HistoricalElement element = new MutationHistoricalElement(variant, base);
        final Variant newVariant = variantStore.createVariant(gene, element);
        pb.step();
        generatedVariants.add(newVariant);

        // 新しい修正プログラムが生成された場合，必要数に達しているかを調べる
        // 達している場合はこれ以上の変異プログラムは生成しない
        if (newVariant.isCompleted()) {
          foundSolutions++;
        }
        if (requiredSolutions <= foundSolutions) {
          break;
        }
      }
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

  public enum Type {
    Simple {
      @Override
      public Mutation initialize(final int mutationGeneratingCount, final Random random,
          final int requiredSolutions,
          final Scope.Type scopeType) {
        final CandidateSelection candidateSelection =
            new RouletteStatementAndConditionSelection(random);
        return new SimpleMutation(mutationGeneratingCount, random, candidateSelection,
            requiredSolutions, scopeType);
      }
    },

    Heuristic {
      @Override
      public Mutation initialize(final int mutationGeneratingCount, final Random random,
          final int requiredSolutions,
          final Scope.Type scopeType) {
        final CandidateSelection candidateSelection =
            new HeuristicStatementSelection(random);
        return new HeuristicMutation(mutationGeneratingCount, random, candidateSelection,
            requiredSolutions,
            scopeType);
      }
    };

    public abstract Mutation initialize(final int mutationGeneratingCount, final Random random,
        final int requiredSolutions, final Scope.Type scopeType);
  }
}
