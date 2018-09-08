package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class DefaultCodeValidation implements SourceCodeValidation {

  private static Logger log = LoggerFactory.getLogger(DefaultCodeValidation.class);

  @Override
  public Fitness exec(final GeneratedSourceCode sourceCode, final TargetProject project,
      final TestExecutor testExecutor) {
    log.debug("enter exec(GeneratedSourceCode, TargetProject, TestProcessBuilder)");

    final TestResults testResults;

    if (shouldTryBuild(sourceCode)) {
      testResults = testExecutor.exec(sourceCode);
    } else {
      testResults = EmptyTestResults.instance;
    }

    return new SimpleFitness(testResults.getSuccessRate());
  }

  private boolean shouldTryBuild(final GeneratedSourceCode sourceCode) {
    return !sourceCode.equals(GenerationFailedSourceCode.instance);
  }

}
