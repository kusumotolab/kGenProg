package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class DefaultCodeValidation implements SourceCodeValidation {

  private static Logger log = LoggerFactory.getLogger(DefaultCodeValidation.class);

  @Override
  public Fitness exec(final VariantStore variantStore, final TestResults testResults) {
    log.debug("enter exec(Variant, TargetProject, TestProcessBuilder)");
    return new SimpleFitness(testResults.getSuccessRate());
  }
}
