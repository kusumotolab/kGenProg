package jp.kusumotolab.kgenprog;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Patch;
import jp.kusumotolab.kgenprog.project.PatchGenerator;
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
    this.patchGenerator = patchGenerator;
  }

  public List<Variant> run() {
    log.debug("enter run()");

    final List<Variant> completedVariants = new ArrayList<>();

    List<Variant> selectedVariants = new ArrayList<>();
    final Variant initialVariant = config.getTargetProject()
        .getInitialVariant();
    selectedVariants.add(initialVariant);

    mutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getAsts());

    final StopWatch stopwatch = new StopWatch(this.config.getTimeLimitSeconds());
    stopwatch.start();
    final OrdinalNumber generation = new OrdinalNumber(1);
    final OrdinalNumber foundSolutions = new OrdinalNumber(0);
    GA: while (true) {

      log.info("in the era of the " + generation.toString() + " generation (" + stopwatch.toString()
          + ")");

      // 遺伝子を生成
      final List<Gene> genes = new ArrayList<>();
      for (final Variant variant : selectedVariants) {
        final List<Suspiciousness> suspiciousnesses =
            faultLocalization.exec(config.getTargetProject(), variant, testExecutor);
        final List<Base> bases = mutation.exec(suspiciousnesses);
        genes.addAll(variant.getGene()
            .generateNextGenerationGenes(bases));
      }
      genes.addAll(crossover.exec(selectedVariants));

      // 遺伝子をもとに変異プログラムを生成
      final List<Variant> currentGenerationVariants = new ArrayList<>();
      for (final Gene gene : genes) {
        final GeneratedSourceCode generatedSourceCode =
            sourceCodeGeneration.exec(gene, config.getTargetProject());
        final Fitness fitness =
            sourceCodeValidation.exec(generatedSourceCode, config.getTargetProject(), testExecutor);
        final Variant variant = new Variant(gene, fitness, generatedSourceCode);
        currentGenerationVariants.add(variant);

        // 生成した変異プログラムが，すべてのテストケースをパスした場合
        if (variant.isCompleted()) {
          foundSolutions.incrementAndGet();

          log.info(foundSolutions.toString() + " solution has been found (" + stopwatch.toString()
              + ")");

          completedVariants.add(variant);

          // しきい値以上の completedVariants が生成された場合は，GA を抜ける
          if (areEnoughCompletedVariants(completedVariants)) {
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
      if (reachedMaxGeneration(generation)) {
        log.info("reached the maximum generation");
        break;
      }

      // 次世代に向けての準備
      selectedVariants = variantSelection.exec(currentGenerationVariants);
      generation.getAndIncrement();
    }

    // 生成されたバリアントのパッチ出力
    logPatch(completedVariants);

    log.debug("exit run()");
    return completedVariants;
  }

  private boolean reachedMaxGeneration(final OrdinalNumber generation) {
    log.debug("enter reachedMaxGeneration(OrdinalNumber)");
    return this.config.getMaxGeneration() <= generation.get();
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    log.debug("enter areEnoughCompletedVariants(List<Variant>)");
    return this.config.getRequiredSolutionsCount() <= completedVariants.size();
  }

  private void logPatch(final List<Variant> completedVariants) {
    log.debug("enter outputPatch(List<Variant>)");
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
