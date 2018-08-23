package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class VariantStore {

  private final TargetProject targetProject;
  private final Strategies strategies;
  private final Variant initialVariant;
  private List<Variant> currentVariants;
  private final List<Variant> foundSolutions;
  private final OrdinalNumber generation;

  public VariantStore(final TargetProject targetProject, final Strategies strategies) {
    this.targetProject = targetProject;
    this.strategies = strategies;

    initialVariant = createInitialVariant();
    currentVariants = Collections.singletonList(initialVariant);
    foundSolutions = new ArrayList<>();
    generation = new OrdinalNumber(1);
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
    foundSolutions = new ArrayList<>();
    generation = new OrdinalNumber(1);
  }

  public Variant createVariant(final Gene gene) {
    final GeneratedSourceCode sourceCode = strategies.execSourceCodeGeneration(this, gene);
    return createVariant(gene, sourceCode);
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

  public void addFoundSolution(final Variant variant) {
    foundSolutions.add(variant);
  }

  public List<Variant> getFoundSolutions() {
    return foundSolutions;
  }

  public void setNextGenerationVariants(final List<Variant> variants) {
    generation.incrementAndGet();
    currentVariants = variants;
  }

  private Variant createInitialVariant() {
    final GeneratedSourceCode sourceCode = strategies.execASTConstruction(targetProject);
    return createVariant(new SimpleGene(Collections.emptyList()), sourceCode);
  }

  private Variant createVariant(final Gene gene, final GeneratedSourceCode sourceCode) {
    final TestResults testResults = strategies.execTestExecutor(sourceCode);
    final Fitness fitness = strategies.execSourceCodeValidation(this, testResults);
    final List<Suspiciousness> suspiciousnesses =
        strategies.execFaultLocalization(sourceCode, testResults);
    return new Variant(gene, sourceCode, testResults, fitness, suspiciousnesses);
  }
}
