package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Variant {

  private static Logger log = LoggerFactory.getLogger(Variant.class);

  private final Gene gene;
  private final GeneratedSourceCode generatedSourceCode;
  private final TestResults testResults;
  private final Fitness fitness;
  private final List<Suspiciousness> suspiciousnesses;
  private final HistoricalElement historicalElement;

  public Variant(final Gene gene, final GeneratedSourceCode generatedSourceCode,
      final TestResults testResults, final Fitness fitness,
      final List<Suspiciousness> suspiciousnesses, final HistoricalElement historicalElement) {
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
