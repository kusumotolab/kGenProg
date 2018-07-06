package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class DefaultCodeValidation implements SourceCodeValidation {

  @Override
  public Fitness exec(GeneratedSourceCode sourceCode, TargetProject project,
      TestProcessBuilder testExecutor) {
    return new SimpleFitness(
        testExecutor.start(sourceCode).orElse(TestResults.EMPTY_VALUE).getSuccessRate());
  }

}
