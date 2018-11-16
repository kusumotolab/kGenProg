package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Variant {

  private final int generationNumber;
  private final Gene gene;
  private final GeneratedSourceCode generatedSourceCode;
  private final TestResults testResults;
  private final Fitness fitness;
  private final List<Suspiciousness> suspiciousnesses;
  private final HistoricalElement historicalElement;

  public Variant(final int generationNumber, final Gene gene,
      final GeneratedSourceCode generatedSourceCode, final TestResults testResults,
      final Fitness fitness, final List<Suspiciousness> suspiciousnesses,
      final HistoricalElement historicalElement) {
    this.generationNumber = generationNumber;
    this.gene = gene;
    this.generatedSourceCode = generatedSourceCode;
    this.testResults = testResults;
    this.fitness = fitness;
    this.suspiciousnesses = suspiciousnesses;
    this.historicalElement = historicalElement;
  }

  public boolean isCompleted() {
    return fitness.isMaximum();
  }

  public boolean isSyntaxValid() {
    return generatedSourceCode.isGenerationSuccess();
  }

  public boolean isBuildSucceeded() {
    return EmptyTestResults.class != testResults.getClass();
  }

  public OrdinalNumber getGenerationNumber() {
    return new OrdinalNumber(generationNumber);
  }

  public Gene getGene() {
    return gene;
  }

  public GeneratedSourceCode getGeneratedSourceCode() {
    return generatedSourceCode;
  }

  public TestResults getTestResults() {
    return testResults;
  }

  public Fitness getFitness() {
    return fitness;
  }

  public List<Suspiciousness> getSuspiciousnesses() {
    return suspiciousnesses;
  }

  public HistoricalElement getHistoricalElement() {
    return historicalElement;
  }
}
