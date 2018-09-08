package jp.kusumotolab.kgenprog;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.ga.VariantStore;
import jp.kusumotolab.kgenprog.project.Patch;
import jp.kusumotolab.kgenprog.project.PatchGenerator;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class KGenProgMain {

  private static Logger log = LoggerFactory.getLogger(KGenProgMain.class);

  private final Configuration config;
  private final FaultLocalization faultLocalization;
  private final Mutation mutation;
  private final Crossover crossover;
  private final SourceCodeGeneration sourceCodeGeneration;
  private final SourceCodeValidation sourceCodeValidation;
  private final VariantSelection variantSelection;
  private final TestExecutor testExecutor;
  private final PatchGenerator patchGenerator;
  private final JDTASTConstruction astConstruction;

  public KGenProgMain(final Configuration config, final FaultLocalization faultLocalization,
      final Mutation mutation, final Crossover crossover,
      final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final VariantSelection variantSelection,
      final PatchGenerator patchGenerator) {

    this.config = config;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testExecutor = new TestExecutor(config);
    this.astConstruction = new JDTASTConstruction();
    this.patchGenerator = patchGenerator;
  }

  public List<Variant> run() {
    log.debug("enter run()");
    final Strategies strategies = new Strategies(faultLocalization, astConstruction,
        sourceCodeGeneration, sourceCodeValidation, testExecutor);
    final VariantStore variantStore = new VariantStore(config.getTargetProject(), strategies);
    final Variant initialVariant = variantStore.getInitialVariant();

    mutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getAsts());

    final StopWatch stopwatch = new StopWatch(config.getTimeLimitSeconds());
    stopwatch.start();

    GA: while (true) {

      log.info("in the era of the " + variantStore.getGenerationNumber()
          .toString() + " generation (" + stopwatch.toString() + ")");

      // 変異プログラムを生成
      final List<Variant> nextGenerationVariants = new ArrayList<>();
      nextGenerationVariants.addAll(mutation.exec(variantStore));
      nextGenerationVariants.addAll(crossover.exec(variantStore));

      for (final Variant variant : nextGenerationVariants) {
        // 生成した変異プログラムが，すべてのテストケースをパスした場合
        if (variant.isCompleted()) {
          variantStore.addFoundSolution(variant);
          log.info(variantStore.getFoundSolutionsNumber()
              .toString() + " solution has been found (" + stopwatch.toString() + ")");

          // しきい値以上の completedVariants が生成された場合は，GA を抜ける
          if (areEnoughCompletedVariants(variantStore.getFoundSolutions())) {
            log.info("reached the required solutions");
            break GA;
          }
        }
      }

      // 制限時間に達した場合には GA を抜ける
      if (stopwatch.isTimeout()) {
        log.info("reached the time limit");
        break;
      }

      // 最大世代数に到達した場合には GA を抜ける
      if (reachedMaxGeneration(variantStore.getGenerationNumber())) {
        log.info("reached the maximum generation");
        break;
      }

      // 次世代に向けての準備
      variantStore.setNextGenerationVariants(
          variantSelection.exec(variantStore.getCurrentVariants(), nextGenerationVariants));
    }

    // 生成されたバリアントのパッチ出力
    logPatch(variantStore);

    log.debug("exit run()");
    return variantStore.getFoundSolutions();
  }

  private boolean reachedMaxGeneration(final OrdinalNumber generation) {
    log.debug("enter reachedMaxGeneration(OrdinalNumber)");
    return config.getMaxGeneration() <= generation.get();
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    log.debug("enter areEnoughCompletedVariants(List<Variant>)");
    return config.getRequiredSolutionsCount() <= completedVariants.size();
  }

  private void logPatch(final VariantStore variantStore) {
    List<Variant> completedVariants = variantStore.getFoundSolutions();
    log.debug("enter outputPatch(VariantStore)");
    for (final Variant completedVariant : completedVariants) {
      final List<Patch> patches = patchGenerator.exec(completedVariant);
      log.info(makeVariantId(completedVariants, completedVariant));
      for (final Patch patch : patches) {
        log.info(System.lineSeparator() + patch.getDiff());
      }
    }
  }

  private String makeVariantId(final List<Variant> variants, final Variant variant) {
    return "variant" + (variants.indexOf(variant) + 1);
  }
}
