package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class VariantStore {

  private static Logger log = LoggerFactory.getLogger(VariantStore.class);

  private final TargetProject targetProject;
  private final Strategies strategies;
  private final Variant initialVariant;
  private List<Variant> currentVariants;
  private List<Variant> generatedVariants;
  private final List<Variant> foundSolutions;
  private final OrdinalNumber generation;

  public VariantStore(final TargetProject targetProject, final Strategies strategies) {
    this.targetProject = targetProject;
    this.strategies = strategies;

    generation = new OrdinalNumber(0);
    initialVariant = createInitialVariant();
    currentVariants = Collections.singletonList(initialVariant);
    generatedVariants = new ArrayList<>();
    foundSolutions = new ArrayList<>();
    generation.incrementAndGet();
  }

  /**
   * テスト用
   */
  @Deprecated
  public VariantStore(final Variant initialVariant) {
    this.targetProject = null;
    this.strategies = null;
    this.initialVariant = initialVariant;

    currentVariants = Collections.singletonList(initialVariant);
    generatedVariants = new ArrayList<>();
    foundSolutions = new ArrayList<>();
    generation = new OrdinalNumber(1);
  }

  public Variant createVariant(final Gene gene, final HistoricalElement element) {
    final GeneratedSourceCode sourceCode = strategies.execSourceCodeGeneration(this, gene);
    return createVariant(gene, sourceCode, element);
  }

  public Variant getInitialVariant() {
    return initialVariant;
  }

  public OrdinalNumber getGenerationNumber() {
    return generation;
  }

  public OrdinalNumber getFoundSolutionsNumber() {
    return new OrdinalNumber(foundSolutions.size());
  }

  public List<Variant> getCurrentVariants() {
    return currentVariants;
  }

  public List<Variant> getGeneratedVariants() {
    return generatedVariants;
  }

  public List<Variant> getFoundSolutions() {
    return foundSolutions;
  }

  public List<Variant> getFoundSolutions(final int maxNumber) {
    final int length = Math.min(maxNumber, foundSolutions.size());
    return foundSolutions.subList(0, length);
  }

  /**
   * 引数の要素すべてを次世代のVariantとして追加する
   *
   * @param variants 追加対象
   * @see addNextGenerationVariant(Variant)
   */
  public void addGeneratedVariants(final Variant... variants) {
    addGeneratedVariants(Arrays.asList(variants));
  }

  /**
   * リストの要素すべてを次世代のVariantとして追加する
   *
   * @param variants 追加対象
   * @see addNextGenerationVariant(Variant)
   */
  public void addGeneratedVariants(final Collection<? extends Variant> variants) {
    variants.forEach(this::addGeneratedVariant);
  }

  /**
   * 引数を次世代のVariantとして追加する {@code variant.isCompleted() == true}
   * の場合，foundSolutionとして追加され次世代のVariantには追加されない
   *
   * @param variant
   */
  public void addGeneratedVariant(final Variant variant) {
    log.debug("enter addNextGenerationVariant(Variant)");

    if (variant.isCompleted()) {
      foundSolutions.add(variant);
      log.info("{} solution has been found", getFoundSolutionsNumber());
    } else {
      generatedVariants.add(variant);
    }
  }

  /**
   * VariantSelectionを実行し世代交代を行う
   *
   * currentVariantsおよびgeneratedVariantsから次世代のVariantsを選択し，それらを次のcurrentVariantsとする
   * また，generatedVariantsをclearする
   */
  public void changeGeneration() {
    log.debug("enter changeGeneration()");

    final List<Variant> nextVariants =
        strategies.execVariantSelection(currentVariants, generatedVariants);
    generation.incrementAndGet();
    log.info("exec selection. {} variants: ({}, {}) => {}", generation, currentVariants.size(),
        generatedVariants.size(), nextVariants.size());

    currentVariants = nextVariants;
    generatedVariants = new ArrayList<>();
  }

  private Variant createInitialVariant() {
    final GeneratedSourceCode sourceCode = strategies.execASTConstruction(targetProject);

    final GeneratedAST ast = sourceCode.getAsts()
        .get(0);
    final CtClass clazz = Launcher.parseClass(ast.getSourceCode());
    final ComplexityScanner scanner = new ComplexityScanner();
    clazz.accept(scanner);
    MetricFitness.init(scanner.getComplexity());

    return createVariant(new Gene(Collections.emptyList()), sourceCode,
        new OriginalHistoricalElement());
  }

  private Variant createVariant(final Gene gene, final GeneratedSourceCode sourceCode,
      final HistoricalElement element) {
    final TestResults testResults = strategies.execTestExecutor(sourceCode);
    final Fitness fitness = strategies.execSourceCodeValidation(sourceCode, testResults);
    final List<Suspiciousness> suspiciousnesses =
        strategies.execFaultLocalization(sourceCode, testResults);
    return new Variant(generation.get(), gene, sourceCode, testResults, fitness, suspiciousnesses,
        element);
  }

}
