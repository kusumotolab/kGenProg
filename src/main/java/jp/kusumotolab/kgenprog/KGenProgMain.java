package jp.kusumotolab.kgenprog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.output.PatchGenerator;
import jp.kusumotolab.kgenprog.output.PatchStore;
import jp.kusumotolab.kgenprog.output.VariantStoreExporter;
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
      final TestExecutor testExecutor, final PatchGenerator patchGenerator) {

    this.config = config;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testExecutor = testExecutor;
    this.astConstruction = new JDTASTConstruction();
    this.patchGenerator = patchGenerator;
  }

  public List<Variant> run() {

    logConfig();

    testExecutor.initialize();

    final Strategies strategies = new Strategies(faultLocalization, astConstruction,
        sourceCodeGeneration, sourceCodeValidation, testExecutor, variantSelection);
    final VariantStore variantStore = new VariantStore(config, strategies);
    final Variant initialVariant = variantStore.getInitialVariant();

    mutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getProductAsts());
    sourceCodeGeneration.initialize(initialVariant);

    final StopWatch stopwatch = new StopWatch(config.getTimeLimitSeconds());
    stopwatch.start();

    while (true) {

      // 新しい世代に入ったことをログ出力
      logGeneration(variantStore.getGenerationNumber());

      // 変異プログラムを生成
      final List<Variant> variantsByMutation = mutation.exec(variantStore);
      final List<Variant> variantsByCrossover = crossover.exec(variantStore);
      variantStore.addGeneratedVariants(variantsByMutation);
      variantStore.addGeneratedVariants(variantsByCrossover);

      // 世代別サマリの出力
      logGenerationSummary(stopwatch.toString(), variantsByMutation, variantsByCrossover);
      stopwatch.split();

      // しきい値以上の completedVariants が生成された場合は，GA を抜ける
      if (areEnoughCompletedVariants(variantStore.getFoundSolutions())) {
        log.info("enough solutions have been found.");
        logGAStopped(variantStore.getGenerationNumber());
        break;
      }

      // 制限時間に達した場合には GA を抜ける
      if (stopwatch.isTimeout()) {
        log.info("GA reached the time limit.");
        logGAStopped(variantStore.getGenerationNumber());
        break;
      }

      // 最大世代数に到達した場合には GA を抜ける
      if (reachedMaxGeneration(variantStore.getGenerationNumber())) {
        log.info("GA reached the maximum generation.");
        logGAStopped(variantStore.getGenerationNumber());
        break;
      }

      // 次世代に向けての準備
      variantStore.proceedNextGeneration();
    }

    // 生成されたバリアントのパッチ出力
    logPatch(variantStore);

    // jsonの出力
    writeJson(variantStore);

    stopwatch.unsplit();
    strategies.finish();
    log.info("execution time: " + stopwatch.toString());


    return variantStore.getFoundSolutions(config.getRequiredSolutionsCount());
  }

  private boolean reachedMaxGeneration(final OrdinalNumber generation) {
    return config.getMaxGeneration() <= generation.get();
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    return config.getRequiredSolutionsCount() <= completedVariants.size();
  }

  private void logPatch(final VariantStore variantStore) {
    final PatchStore patchStore = new PatchStore();
    final List<Variant> completedVariants =
        variantStore.getFoundSolutions(config.getRequiredSolutionsCount());

    for (final Variant completedVariant : completedVariants) {
      patchStore.add(patchGenerator.exec(completedVariant));
    }

    patchStore.writeToLogger();

    if (!config.needNotOutput()) {
      patchStore.writeToFile(config.getOutDir());
    }
  }

  private void writeJson(final VariantStore variantStore) {
    final VariantStoreExporter variantStoreExporter = new VariantStoreExporter();

    if (!config.needNotOutput()) {
      variantStoreExporter.writeToFile(config, variantStore);
    }
  }

  private void logConfig() {
    final StringBuilder sb = new StringBuilder();
    sb//
        .append(System.lineSeparator())
        .append("==================== kGenProg Configuration ====================")
        .append(System.lineSeparator())
        .append(config.toString())
        .append("currentDirectory = ")
        .append(System.getProperty("user.dir"))
        .append(System.lineSeparator())
        .append("================================================================");
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
    sb//
        .append(System.lineSeparator())
        .append("----------------------------------------------------------------")
        .append(System.lineSeparator())
        .append("Elapsed time: ")
        .append(timeText)
        .append(System.lineSeparator())
        .append("Variants: generated ")
        .append(variantsByMutation.size() + variantsByCrossover.size())
        .append(", syntax-valid ")
        .append(count(variants, v -> v.isSyntaxValid()))
        .append(", build-succeeded ")
        .append(count(variants, v -> v.isBuildSucceeded()))
        .append(System.lineSeparator())
        .append("Fitness: max ")
        .append(getMaxText(variants))
        .append(", min ")
        .append(getMinText(variants))
        .append(", ave ")
        .append(getAverage(variants))
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
    return max.getKey() + "(" + max.getValue() + ")";
  }

  private String getMinText(final List<Variant> variants) {
    final Map<Double, Long> frequencies = getFrequencies(variants);
    if (frequencies.isEmpty()) {
      return "--";
    }
    final Map.Entry<Double, Long> min =
        Collections.min(frequencies.entrySet(), Map.Entry.comparingByKey());
    return min.getKey() + "(" + min.getValue() + ")";
  }

  private double getAverage(final List<Variant> variants) {
    return variants.stream()
        .filter(v -> v.isBuildSucceeded())
        .mapToDouble(v -> getFitnessValue(v))
        .average()
        .orElse(Double.NaN);
  }

  private Map<Double, Long> getFrequencies(final List<Variant> variants) {
    return variants.stream()
        .filter(v -> v.isBuildSucceeded())
        .collect(Collectors.groupingBy(v -> getFitnessValue(v), Collectors.counting()));
  }

  private double getFitnessValue(final Variant variant) {
    return variant.getFitness()
        .getValue();
  }

  private void logGAStopped(final OrdinalNumber generation) {
    final StringBuilder sb = new StringBuilder();
    sb//
        .append("GA stopped at the era of ")
        .append(generation.toString())
        .append(" generation.");
    log.info(sb.toString());
  }
}
