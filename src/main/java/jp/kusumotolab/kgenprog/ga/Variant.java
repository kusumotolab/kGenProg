package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Variant {

  private static Logger log = LoggerFactory.getLogger(Variant.class);

  private final int generationNumber;
  private final Gene gene;
  private final GeneratedSourceCode generatedSourceCode;
  private final TestResults testResults;
  private final Fitness fitness;
  private final List<Suspiciousness> suspiciousnesses;
  private final HistoricalElement historicalElement;

  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Variant variant = (Variant) o;
    return generationNumber == variant.generationNumber &&
        Objects.equals(gene, variant.gene) &&
        Objects.equals(generatedSourceCode, variant.generatedSourceCode) &&
        Objects.equals(testResults, variant.testResults) &&
        Objects.equals(fitness, variant.fitness) &&
        Objects.equals(suspiciousnesses, variant.suspiciousnesses);
  }

  @Override
  public int hashCode() {
    final int prime = 31;

    int result = 1;
    result = result * prime + generationNumber;
    result = result * prime + gene.hashCode();
    result = result * prime + testResults.hashCode();
    result = result * prime + fitness.hashCode();
    result = result * prime + suspiciousnesses.hashCode();

    return result;
  }

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

  public OrdinalNumber getGenerationNumber() {
    log.debug("enter getGenerationNumberF()");
    return new OrdinalNumber(generationNumber);
  }

  public Gene getGene() {
    log.debug("enter getGene()");
    return gene;
  }

  public GeneratedSourceCode getGeneratedSourceCode() {
    log.debug("enter getGeneratedSourceCode()");
    return generatedSourceCode;
  }

  public TestResults getTestResults() {
    log.debug("enter getTestResults()");
    return testResults;
  }

  public Fitness getFitness() {
    log.debug("enter getFitness()");
    return fitness;
  }

  public List<Suspiciousness> getSuspiciousnesses() {
    log.debug("enter getSuspiciousnesses");
    return suspiciousnesses;
  }

  public HistoricalElement getHistoricalElement() {
    log.debug("enter getHistoricalElement");
    return historicalElement;
  }
}
