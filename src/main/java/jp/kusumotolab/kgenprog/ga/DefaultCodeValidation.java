package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class DefaultCodeValidation implements SourceCodeValidation {

  @Override
  public Fitness exec(final GeneratedSourceCode sourceCode, final TestResults testResults) {
    return new SimpleFitness(testResults.getSuccessRate());
  }
}
