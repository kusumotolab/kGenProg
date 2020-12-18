package jp.kusumotolab.kgenprog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.output.Exporters;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * kGenProgのメインクラス．<br>
 * このクラスのインスタンスを生成し，runメソッドを実行することで，自動プログラム修正を行う．<br>
 * コマンドラインからの実行には{@link CUILauncher}}クラスを用いる．<br>
 *
 * @author higo
 */
public class KGenProgMain {

  private final static Logger log = LoggerFactory.getLogger(KGenProgMain.class);
  private final LogWriter logwriter = new LogWriter();

  private final Configuration config;
  private final Mutation mutation;
  private final Crossover crossover;
  private final SourceCodeGeneration sourceCodeGeneration;
  private final Exporters exporters;
  private final Strategies strategies;

  /**
   * コンストラクタ．自動プログラム修正に必要な全ての情報を渡す必要あり．
   *
   * @param config 設定情報
   * @param faultLocalization 自動バグ限局を行うインスタンス
   * @param mutation 変異を行うインスタンス
   * @param crossover 交叉を行うインスタンス
   * @param sourceCodeGeneration コード生成を行うインスタンス
   * @param sourceCodeValidation コード評価を行うインスタンス
   * @param variantSelection 個体の選択を行うインスタンス
   * @param testExecutor テスト実行を行うインスタンス
   * @param exporters 出力処理を行うインスタンス
   */
  public KGenProgMain(
      final Configuration config,
      final FaultLocalization faultLocalization,
      final Mutation mutation,
      final Crossover crossover,
      final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation,
      final VariantSelection variantSelection,
      final TestExecutor testExecutor,
      final Exporters exporters) {
    this.config = config;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.exporters = exporters;
    this.strategies = new Strategies(faultLocalization, new JDTASTConstruction(),
        sourceCodeGeneration, sourceCodeValidation, testExecutor, variantSelection);
  }

  /**
   * 自動プログラム修正を実行する．<br>
   * 得られた解（全てのテストケースを通過するプログラム）を返す．<br>
   */
  public ExitStatus run() {
    logwriter.logConfig();

    if (!config.getTargetProject()
        .isValid()) {
      log.error("No such project directory.");
      return ExitStatus.FAILURE_INVALID_PROJECT;
    }

    final VariantStore variantStore = new VariantStore(config, strategies);
    final Variant initialVariant = variantStore.getInitialVariant();

    if (!initialVariant.isBuildSucceeded()) {
      log.error("Failed to build the specified project.");
      log.error(System.lineSeparator()); // keep empty line to show build failure causes
      log.error(((EmptyTestResults) initialVariant.getTestResults()).getCause());
      return ExitStatus.FAILURE_INITIAL_BUILD;
    }

    if (initialVariant.isCompleted()) {
      log.error("No bugs to be repaired. All tests passed.");
      return ExitStatus.FAILURE_NO_BUGS;
    }

    logwriter.logInitialFailedTests(initialVariant.getTestResults());

    mutation.setInitialCandidates(initialVariant);
    sourceCodeGeneration.initialize(initialVariant);

    final StopWatch stopwatch = new StopWatch(config.getTimeLimitSeconds());
    stopwatch.start();

    // GAのメインループ
    log.info("GA started");
    final ExitStatus exitStatus = execGALoop(variantStore, stopwatch);
    log.info("GA stopped");

    exporters.exportAll(variantStore);

    stopwatch.unsplit();
    logwriter.logGAStopped(variantStore, stopwatch, exitStatus);

    return exitStatus;
  }


  private ExitStatus execGALoop(final VariantStore variantStore, final StopWatch stopwatch) {
    while (true) {
      // 新しい世代に入ったことをログ出力
      logwriter.logGeneration(variantStore.getGenerationNumber());

      // 変異プログラムを生成
      final List<Variant> variantsByMutation = mutation.exec(variantStore);
      variantStore.addGeneratedVariants(variantsByMutation);
      final List<Variant> variantsByCrossover = crossover.exec(variantStore);
      variantStore.addGeneratedVariants(variantsByCrossover);

      // 世代別サマリの出力
      logwriter.logGenerationSummary(stopwatch.toString(), variantsByMutation, variantsByCrossover);
      stopwatch.split();

      variantStore.updateVariantCounts(
          Stream.concat(variantsByMutation.stream(), variantsByCrossover.stream())
              .collect(Collectors.toList()));

      // しきい値以上の completedVariants が生成された場合は，GA を抜ける
      if (areEnoughCompletedVariants(variantStore.getFoundSolutions())) {
        return ExitStatus.SUCCESS;
      }

      // 制限時間に達した場合には GA を抜ける
      if (stopwatch.isTimeout()) {
        return ExitStatus.FAILURE_TIME_LIMIT;
      }

      // 最大世代数に到達した場合には GA を抜ける
      if (reachedMaxGeneration(variantStore.getGenerationNumber())) {
        return ExitStatus.FAILURE_MAXIMUM_GENERATION;
      }

      // 次世代に向けての準備
      variantStore.proceedNextGeneration();
    }
  }

  private boolean reachedMaxGeneration(final OrdinalNumber generation) {
    return config.getMaxGeneration() <= generation.get();
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    return config.getRequiredSolutionsCount() <= completedVariants.size();
  }

  /**
   * kGenProgMainクラスの終了状態
   */
  enum ExitStatus {
    SUCCESS("SUCCESS"),
    FAILURE_MAXIMUM_GENERATION("FAILURE (maximum generation)"),
    FAILURE_TIME_LIMIT("FAILURE (time limit)"),
    FAILURE_INITIAL_BUILD("FAILURE (initial build failed)"),
    FAILURE_INVALID_PROJECT("FAILURE (invalid project)"),
    FAILURE_NO_BUGS("FAILURE (no bugs to be repaired)");

    private final String code;

    ExitStatus(String code) {
      this.code = code;
    }

    String getCode() {
      return code;
    }
  }

  /**
   * Log出力周りの管理クラス
   */
  private class LogWriter {

    private final DecimalFormat format = new DecimalFormat("#.###");

    private void logConfig() {
      final StringBuilder sb = new StringBuilder()
          .append(System.lineSeparator())
          .append("==================== kGenProg Configuration ====================")
          .append(System.lineSeparator())
          .append(config.toString())
          .append("================================================================");
      log.info(sb.toString());
    }

    private void logGeneration(final OrdinalNumber generation) {
      log.info("entered the era of {} generation.", generation);
    }

    private void logInitialFailedTests(final TestResults testResults) {
      final List<TestResult> succeededResults = testResults.getSucceededTestResults();
      final List<TestResult> failedResults = testResults.getFailedTestResults();

      final StringBuilder sb = new StringBuilder()
          .append(String.format("initial failed tests (%d/%d)",
              failedResults.size(),
              succeededResults.size() + failedResults.size()))
          .append(System.lineSeparator());
      testResults.getFailedTestResults()
          .forEach(r -> sb.append(String.format("%s: %s", r.executedTestFQN, r.getFailedReason())));
      log.info(sb.toString());
    }

    private void logGenerationSummary(final String timeText, final List<Variant> variantsByMutation,
        final List<Variant> variantsByCrossover) {
      final List<Variant> variants = new ArrayList<>();
      variants.addAll(variantsByMutation);
      variants.addAll(variantsByCrossover);
      final StringBuilder sb = new StringBuilder()
          .append(System.lineSeparator())
          .append("----------------------------------------------------------------")
          .append(System.lineSeparator())
          .append(String.format("Elapsed time: %s", timeText))
          .append(System.lineSeparator())
          .append(createVariantsSummary(variants))
          .append(System.lineSeparator())
          .append(createFitnessSummary(variants))
          .append(System.lineSeparator())
          .append(createTestTimeSummary(variants))
          .append(System.lineSeparator())
          .append("----------------------------------------------------------------")
          .append(System.lineSeparator());
      log.info(sb.toString());
    }

    private String createFitnessSummary(final List<Variant> variants) {
      return String.format("Fitness: max %s, min %s, ave %s",
          getMaxText(variants),
          getMinText(variants),
          format.format(getAverage(variants)));
    }

    private String createVariantsSummary(final List<Variant> variants) {
      return String.format(
          "Variants: generated %d, build-succeeded %d, build-failed %d, syntax-invalid %d, redundant %d",
          count(variants, v -> true),
          count(variants, Variant::isBuildSucceeded),
          count(variants, v -> v.triedBuild() && !v.isBuildSucceeded()),
          count(variants, v -> !v.isSyntaxValid() && !v.isReproduced()),
          count(variants, Variant::isReproduced));
    }

    private String createTestTimeSummary(final List<Variant> variants) {
      return String.format(
          "Test execution time: sum %s ms, max %s ms, min %s ms",
          getSumTestTime(variants),
          getMaxTestTime(variants),
          getMinTestTime(variants));
    }

    private String getSumTestTime(final List<Variant> variants) {
      return format.format(
          variants.stream()
              .mapToDouble(e -> e.getTestResults()
                  .getTestTime())
              .filter(e -> Double.compare(e, Double.NaN) != 0)
              .sum());
    }

    private String getMaxTestTime(final List<Variant> variants) {
      final OptionalDouble max = variants.stream()
          .mapToDouble(e -> e.getTestResults()
              .getTestTime())
          .filter(e -> Double.compare(e, Double.NaN) != 0)
          .max();
      return max.isEmpty() ? "--" : format.format(max.orElse(Double.NaN));
    }

    private String getMinTestTime(final List<Variant> variants) {
      final OptionalDouble min = variants.stream()
          .mapToDouble(e -> e.getTestResults()
              .getTestTime())
          .filter(e -> Double.compare(e, Double.NaN) != 0)
          .min();
      return min.isEmpty() ? "--" : format.format(min.orElse(Double.NaN));
    }

    private int count(final List<Variant> variants, final Predicate<Variant> p) {
      return (int) variants.stream()
          .filter(p)
          .count();
    }

    private String getMaxText(final List<Variant> variants) {
      final Map<Double, Long> frequencies = getFrequencies(variants);
      if (frequencies.isEmpty()) {
        return "--";
      }
      final Map.Entry<Double, Long> max =
          Collections.max(frequencies.entrySet(), Map.Entry.comparingByKey());
      return format.format(max.getKey()) + "(" + max.getValue() + ")";
    }

    private String getMinText(final List<Variant> variants) {
      final Map<Double, Long> frequencies = getFrequencies(variants);
      if (frequencies.isEmpty()) {
        return "--";
      }
      final Map.Entry<Double, Long> min =
          Collections.min(frequencies.entrySet(), Map.Entry.comparingByKey());
      return format.format(min.getKey()) + "(" + min.getValue() + ")";
    }

    private double getAverage(final List<Variant> variants) {
      return variants.stream()
          .filter(Variant::isBuildSucceeded)
          .mapToDouble(this::getNormalizedFitnessValue)
          .average()
          .orElse(Double.NaN);
    }

    private Map<Double, Long> getFrequencies(final List<Variant> variants) {
      return variants.stream()
          .filter(Variant::isBuildSucceeded)
          .collect(Collectors.groupingBy(this::getNormalizedFitnessValue, Collectors.counting()));
    }

    private double getNormalizedFitnessValue(final Variant variant) {
      return variant.getFitness()
          .getNormalizedValue();
    }

    private void logGAStopped(final VariantStore variantStore, final StopWatch stopwatch,
        final ExitStatus exitStatus) {
      logGAStopped(variantStore.getGenerationNumber(),
          variantStore.getVariantCount(),
          variantStore.getSyntaxValidVariantCount(),
          variantStore.getBuildSuccessVariantCount(),
          stopwatch.toString(),
          exitStatus);
    }

    private void logGAStopped(final OrdinalNumber generation, final int variantCount,
        final int syntaxValidCount, final int buildSuccessCount, final String time,
        final ExitStatus exitStatus) {
      final StringBuilder sb = new StringBuilder()
          .append("Summary")
          .append(System.lineSeparator())
          .append(String.format("Reached generation = %d", generation.intValue()))
          .append(System.lineSeparator())
          .append(String.format("Generated variants = %d", variantCount))
          .append(System.lineSeparator())
          .append(String.format("Syntax valid variants = %d", syntaxValidCount))
          .append(System.lineSeparator())
          .append(String.format("Build succeeded variants = %d", buildSuccessCount))
          .append(System.lineSeparator())
          .append(String.format("Time elapsed = %s", time))
          .append(System.lineSeparator())
          .append(String.format("Exit status = %s", exitStatus.getCode()));
      log.info(sb.toString());
    }
  }

}
