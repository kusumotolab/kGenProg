package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public class Variant {

  private static Logger log = LoggerFactory.getLogger(Variant.class);

  private final Gene gene;
  private final Fitness fitness;
  private final GeneratedSourceCode generatedSourceCode;

  public Variant(final Gene gene, final Fitness fitness,
      final GeneratedSourceCode generatedSourceCode) {
    this.gene = gene;
    this.fitness = fitness;
    this.generatedSourceCode = generatedSourceCode;
  }

  public boolean isCompleted() {
    return fitness.isMaximum();
  }

  public Gene getGene() {
    log.debug("enter getGene()");
    return gene;
  }

  public Fitness getFitness() {
    log.debug("enter getFitness()");
    return fitness;
  }

  public GeneratedSourceCode getGeneratedSourceCode() {
    log.debug("enter getGeneratedSourceCode()");
    return generatedSourceCode;
  }
}
