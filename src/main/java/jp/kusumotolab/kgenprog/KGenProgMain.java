package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
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
import jp.kusumotolab.kgenprog.project.Result;
import jp.kusumotolab.kgenprog.project.ResultGenerator;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class KGenProgMain {

  private static Logger log = LoggerFactory.getLogger(KGenProgMain.class);

  private final TargetProject targetProject;
  private final FaultLocalization faultLocalization;
  private final Mutation mutation;
  private final Crossover crossover;
  private final SourceCodeGeneration sourceCodeGeneration;
  private final SourceCodeValidation sourceCodeValidation;
  private final VariantSelection variantSelection;
  private final TestProcessBuilder testProcessBuilder;
  private final ResultGenerator resultGenerator;

  // 以下，一時的なフィールド #146 で解決すべき
  private final long timeoutSeconds;
  private final int maxGeneration;
  private final int requiredSolutions;

  // TODO #146
  // workingdirのパスを一時的にMainに記述
  // 別クラスが管理すべき情報？
  public final Path workingPath;

  public KGenProgMain(final TargetProject targetProject, final FaultLocalization faultLocalization,
      final Mutation mutation, final Crossover crossover,
      final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final VariantSelection variantSelection,
      final ResultGenerator resultGenerator, final Path workingPath) {

    this(targetProject, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, resultGenerator, workingPath, 60, 10, 1);
  }

  public KGenProgMain(final TargetProject targetProject, final FaultLocalization faultLocalization,
      final Mutation mutation, final Crossover crossover,
      final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final VariantSelection variantSelection,
      final ResultGenerator resultGenerator, final Path workingPath, final long timeout,
      final int maxGeneration, final int requiredSolutions) {

    this.workingPath = workingPath;
    try {
      if (Files.exists(this.workingPath)) {
        FileUtils.deleteDirectory(this.workingPath.toFile());
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }

    this.targetProject = targetProject;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testProcessBuilder = new TestProcessBuilder(targetProject, this.workingPath);
    this.resultGenerator = resultGenerator;

    this.timeoutSeconds = timeout;
    this.maxGeneration = maxGeneration;
    this.requiredSolutions = requiredSolutions;
  }

  public List<Variant> run() {
    log.debug("enter run()");

    final List<Variant> completedVariants = new ArrayList<>();

    List<Variant> selectedVariants = new ArrayList<>();
    final Variant initialVariant = targetProject.getInitialVariant();
    selectedVariants.add(initialVariant);

    mutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getAsts());

    final StopWatch stopwatch = new StopWatch(timeoutSeconds);
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
            faultLocalization.exec(targetProject, variant, testProcessBuilder);
        final List<Base> bases = mutation.exec(suspiciousnesses);
        genes.addAll(variant.getGene()
            .generateNextGenerationGenes(bases));
      }
      genes.addAll(crossover.exec(selectedVariants));

      // 遺伝子をもとに変異プログラムを生成
      final List<Variant> currentGenerationVariants = new ArrayList<>();
      for (final Gene gene : genes) {
        final GeneratedSourceCode generatedSourceCode =
            sourceCodeGeneration.exec(gene, targetProject);
        final Fitness fitness =
            sourceCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);
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

    outputPatch(completedVariants);

    log.debug("exit run()");
    return completedVariants;
  }

  private boolean reachedMaxGeneration(final OrdinalNumber generation) {
    log.debug("enter reachedMaxGeneration(OrdinalNumber)");
    return this.maxGeneration <= generation.get();
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    log.debug("enter areEnoughCompletedVariants(List<Variant>)");
    return this.requiredSolutions <= completedVariants.size();
  }

  private void outputPatch(List<Variant> completedVariants) {
    for (final Variant completedVariant : completedVariants) {
      final List<Result> results = resultGenerator.exec(targetProject, completedVariant);
      log.info("variant" + (completedVariants.indexOf(completedVariant) + 1));
      for (final Result result : results) {
        log.info(result.getDiff());
      }
    }
  }
}
