package jp.kusumotolab.kgenprog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

  private static Logger log = LoggerFactory.getLogger(KGenProgMain.class);

  private final Configuration config;
  private final FaultLocalization faultLocalization;
  private final Mutation mutation;
  private final Crossover crossover;
  private final SourceCodeGeneration sourceCodeGeneration;
  private final SourceCodeValidation sourceCodeValidation;
  private final VariantSelection variantSelection;
  private final TestExecutor testExecutor;
  private final Exporters exporters;
  private final JDTASTConstruction astConstruction;

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
  public KGenProgMain(final Configuration config, final FaultLocalization faultLocalization,
      final Mutation mutation, final Crossover crossover,
      final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final VariantSelection variantSelection,
      final TestExecutor testExecutor, final Exporters exporters) {

    this.config = config;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testExecutor = testExecutor;
    this.astConstruction = new JDTASTConstruction();
    this.exporters = exporters;
  }

  /**
   * 自動プログラム修正を実行する．<br>
   * 得られた解（全てのテストケースを通過するプログラム）を返す．<br>
   *
   * @return 得られた解（全てのテストケースを通過するプログラム）
   */
  public List<Variant> run() throws RuntimeException {
    logConfig();

    testExecutor.initialize();

    final Strategies strategies = new Strategies(faultLocalization, astConstruction,
        sourceCodeGeneration, sourceCodeValidation, testExecutor, variantSelection);
    final VariantStore variantStore = new VariantStore(config, strategies);
    final Variant initialVariant = variantStore.getInitialVariant();

    logInitialFailedTests(initialVariant.getTestResults());

    mutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getProductAsts());
    sourceCodeGeneration.initialize(initialVariant);

    final StopWatch stopwatch = new StopWatch(config.getTimeLimitSeconds());
    stopwatch.start();

    ExitStatus exitStatus;

    while (true) {

      // 新しい世代に入ったことをログ出力
      logGeneration(variantStore.getGenerationNumber());

      // 変異プログラムを生成
      final List<Variant> variantsByMutation = mutation.exec(variantStore,
          config.getRequiredSolutionsCount());
      variantStore.addGeneratedVariants(variantsByMutation);
      final List<Variant> variantsByCrossover = crossover.exec(variantStore,
          config.getRequiredSolutionsCount());
      variantStore.addGeneratedVariants(variantsByCrossover);

      // 世代別サマリの出力
      logGenerationSummary(stopwatch.toString(), variantsByMutation, variantsByCrossover);
      stopwatch.split();
      variantStore.updateVariantCounts(
          Stream.concat(variantsByMutation.stream(), variantsByCrossover.stream())
              .collect(Collectors.toList()));

      // しきい値以上の completedVariants が生成された場合は，GA を抜ける
      if (areEnoughCompletedVariants(variantStore.getFoundSolutions())) {
        exitStatus = ExitStatus.SUCCESS;
        break;
      }

      // 制限時間に達した場合には GA を抜ける
      if (stopwatch.isTimeout()) {
        exitStatus = ExitStatus.FAILURE_TIME_LIMIT;
        break;
      }

      // 最大世代数に到達した場合には GA を抜ける
      if (reachedMaxGeneration(variantStore.getGenerationNumber())) {
        exitStatus = ExitStatus.FAILURE_MAXIMUM_GENERATION;
        break;
      }

      // 次世代に向けての準備
      variantStore.proceedNextGeneration();
    }

    log.info("GA stopped.");
    // 出力処理を行う
    exporters.exportAll(variantStore);

    stopwatch.unsplit();
    strategies.finish();
    logGAStopped(variantStore.getGenerationNumber(), variantStore.getVariantCount(),
        variantStore.getSyntaxValidVariantCount(), variantStore.getBuildSuccessVariantCount(),
        stopwatch.toString(), exitStatus);

    return variantStore.getFoundSolutions(config.getRequiredSolutionsCount());
  }

  private boolean reachedMaxGeneration(final OrdinalNumber generation) {
    return config.getMaxGeneration() <= generation.get();
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    return config.getRequiredSolutionsCount() <= completedVariants.size();
  }

  private void logConfig() {
    final StringBuilder sb = new StringBuilder();
    sb//
        .append(System.lineSeparator())
        .append("==================== kGenProg Configuration ====================")
        .append(System.lineSeparator())
        .append(config.toString())
        .append("================================================================");
    log.info(sb.toString());
  }

  private void logInitialFailedTests(final TestResults testResults) {
    final StringBuilder sb = new StringBuilder();
    final List<TestResult> succeededTestResults = testResults.getSucceededTestResults();
    final List<TestResult> failedTestResults = testResults.getFailedTestResults();
    sb//
        .append("initial failed tests (")
        .append(failedTestResults.size())
        .append("/")
        .append(succeededTestResults.size() + failedTestResults.size())
        .append(")")
        .append(System.lineSeparator());

    for (TestResult testResult : testResults.getFailedTestResults()) {
      sb//
          .append(testResult.executedTestFQN)
          .append(": ")
          .append(testResult.getFailedReason())
          .append(System.lineSeparator());
    }
    log.info(sb.toString());
  }

  private void logGeneration(final OrdinalNumber generation) {
    final StringBuilder sb = new StringBuilder();
    sb//
        .append("entered the era of ")
        .append(generation.toString())
        .append(" generation.");
    log.info(sb.toString());
  }

  private void logGenerationSummary(final String timeText, final List<Variant> variantsByMutation,
      final List<Variant> variantsByCrossover) {
    final List<Variant> variants = new ArrayList<>();
    variants.addAll(variantsByMutation);
    variants.addAll(variantsByCrossover);
    final StringBuilder sb = new StringBuilder();
    final DecimalFormat df = createDecimalFormat();
    sb//
        .append(System.lineSeparator())
        .append("----------------------------------------------------------------")
        .append(System.lineSeparator())
        .append("Elapsed time: ")
        .append(timeText)
        .append(System.lineSeparator())
        .append("Variants: generated ")
        .append(variantsByMutation.size() + variantsByCrossover.size())
        .append(", build-succeeded ")
        .append(count(variants, Variant::isBuildSucceeded))
        .append(", build-failed ")
        .append(count(variants, v -> v.triedBuild() && !v.isBuildSucceeded()))
        .append(", syntax-invalid ")
        .append(count(variants, v -> !v.isSyntaxValid() && !v.isReproduced()))
        .append(", redundant ")
        .append(count(variants, Variant::isReproduced))
        .append(System.lineSeparator())
        .append("Fitness: max ")
        .append(getMaxText(variants))
        .append(", min ")
        .append(getMinText(variants))
        .append(", ave ")
        .append(df.format(getAverage(variants)))
        .append(System.lineSeparator())
        .append("----------------------------------------------------------------")
        .append(System.lineSeparator());
    log.info(sb.toString());
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
    final DecimalFormat df = createDecimalFormat();
    return df.format(max.getKey()) + "(" + max.getValue() + ")";
  }

  private String getMinText(final List<Variant> variants) {
    final Map<Double, Long> frequencies = getFrequencies(variants);
    if (frequencies.isEmpty()) {
      return "--";
    }
    final Map.Entry<Double, Long> min =
        Collections.min(frequencies.entrySet(), Map.Entry.comparingByKey());
    final DecimalFormat df = createDecimalFormat();
    return df.format(min.getKey()) + "(" + min.getValue() + ")";
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

  private DecimalFormat createDecimalFormat() {
    return new DecimalFormat("#.###");
  }

  private void logGAStopped(final OrdinalNumber generation, final int variantCount,
      final int syntaxValidCount, final int buildSuccessCount, final String time,
      final ExitStatus exitStatus) {
    final StringBuilder sb = new StringBuilder();
    sb//
        .append("Summary")
        .append(System.lineSeparator())
        .append("Reached generation = ")
        .append(generation.intValue())
        .append(System.lineSeparator())
        .append("Generated variants = ")
        .append(variantCount)
        .append(System.lineSeparator())
        .append("Syntax valid variants = ")
        .append(syntaxValidCount)
        .append(System.lineSeparator())
        .append("Build succeeded variants = ")
        .append(buildSuccessCount)
        .append(System.lineSeparator())
        .append("Time elapsed = ")
        .append(time)
        .append(System.lineSeparator())
        .append("Exit status = ")
        .append(exitStatus.getCode());
    log.info(sb.toString());
  }

  private enum ExitStatus {
    SUCCESS("SUCCESS"), FAILURE_MAXIMUM_GENERATION(
        "FAILURE (maximum generation)"), FAILURE_TIME_LIMIT("FAILURE (time limit)");
    private final String code;

    ExitStatus(String code) {
      this.code = code;
    }

    String getCode() {
      return code;
    }
  }
}
