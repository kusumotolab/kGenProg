package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
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
import jp.kusumotolab.kgenprog.project.ResultOutput;
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
  private final ResultOutput resultGenerator;

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
      final ResultOutput resultGenerator, final Path workingPath) {

    this(targetProject, faultLocalization, mutation, crossover, sourceCodeGeneration,
        sourceCodeValidation, variantSelection, resultGenerator, workingPath, 60, 10, 1);
  }

  public KGenProgMain(final TargetProject targetProject, final FaultLocalization faultLocalization,
      final Mutation mutation, final Crossover crossover,
      final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final VariantSelection variantSelection,
      final ResultOutput resultGenerator, final Path workingPath, final long timeout,
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
        .getFiles());
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    int generation = 0;
    int foundSolutions = 0;
    GA: while (true) {

      // 制限時間に達したか，最大世代数に到達した場合には GA を抜ける
      if (isTimedOut(stopWatch) || reachedMaxGeneration(generation++)) {
        break;
      }

      log.info("in the era of the " + getOrdinalNumber(generation) + " generation ("
          + getExecutionTime(stopWatch) + ")");

      final List<Gene> genes = new ArrayList<>();
      for (final Variant variant : selectedVariants) {
        final List<Suspiciouseness> suspiciousenesses =
            faultLocalization.exec(targetProject, variant, testProcessBuilder);

        final List<Base> bases = mutation.exec(suspiciousenesses);
        genes.addAll(variant.getGene()
            .generateNextGenerationGenes(bases));
      }

      genes.addAll(crossover.exec(selectedVariants));

      final List<Variant> currentGenerationVariants = new ArrayList<>();
      for (final Gene gene : genes) {
        final GeneratedSourceCode generatedSourceCode =
            sourceCodeGeneration.exec(gene, targetProject);

        final Fitness fitness =
            sourceCodeValidation.exec(generatedSourceCode, targetProject, testProcessBuilder);

        final Variant variant = new Variant(gene, fitness, generatedSourceCode);
        currentGenerationVariants.add(variant);

        if (0 == Double.compare(fitness.getValue(), 1.0d)) {

          log.info(getOrdinalNumber(++foundSolutions) + " solution has been found ("
              + getExecutionTime(stopWatch) + ")");

          completedVariants.add(variant);

          // しきい値以上の completedVariants が生成された場合は，GAを抜ける
          if (areEnoughCompletedVariants(completedVariants)) {
            break GA;
          }
        }
      }

      selectedVariants = variantSelection.exec(currentGenerationVariants);
    }

    resultGenerator.outputResult(targetProject, completedVariants);

    log.debug("exit run()");
    return completedVariants;
  }

  private boolean reachedMaxGeneration(final int generation) {
    log.debug("enter reachedMaxGeneration()");
    return this.maxGeneration <= generation;
  }

  private boolean isTimedOut(final StopWatch stopWatch) {
    log.debug("enter isTimedOut()");
    final long elapsedSeconds = stopWatch.getTime(TimeUnit.SECONDS);
    return elapsedSeconds > this.timeoutSeconds;
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    return this.requiredSolutions <= completedVariants.size();
  }

  /**
   * 基数を序数に変換する．
   * 
   * @param cardinalNumber 変換したい基数
   * @return 序数の文字列
   */
  public static String getOrdinalNumber(int cardinalNumber) {

    // "st"をつける場合．11は対象外．
    if ((cardinalNumber % 10 == 1) && (cardinalNumber % 100 != 11)) {
      return cardinalNumber + "st";
    }

    // "nd"をつける場合．12は対象外．
    else if ((cardinalNumber % 10 == 2) && (cardinalNumber % 100 != 12)) {
      return cardinalNumber + "nd";
    }

    // "rd"をつける場合．13の場合は対象外．
    else if ((cardinalNumber % 10 == 3) && (cardinalNumber % 100 != 13)) {
      return cardinalNumber + "rd";
    }

    // "th"をつける場合．上記の以外すべて．
    else {
      return cardinalNumber + "th";
    }
  }

  public static String getExecutionTime(final StopWatch stopWatch) {

    final long elapsedSeconds = stopWatch.getTime(TimeUnit.SECONDS);

    final long hours = elapsedSeconds / 3600;
    final long minutes = (elapsedSeconds % 3600) / 60;
    final long seconds = (elapsedSeconds % 3600) % 60;

    final StringBuilder text = new StringBuilder();
    if (0 < hours) {
      text.append(hours);
      text.append(" hours ");
    }
    if (0 < minutes) {
      text.append(minutes);
      text.append(" minutes ");
    }
    text.append(seconds);
    text.append(" seconds");

    return text.toString();
  }
}
