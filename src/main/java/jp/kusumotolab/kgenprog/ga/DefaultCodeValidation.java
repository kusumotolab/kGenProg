package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public class DefaultCodeValidation implements SourceCodeValidation {

  private static Logger log = LoggerFactory.getLogger(DefaultCodeValidation.class);

  @Override
  public Fitness exec(GeneratedSourceCode sourceCode, TargetProject project,
      TestProcessBuilder testExecutor) {
    log.debug("enter exec(GeneratedSourceCode, TargetProject, TestProcessBuilder)");
    return new SimpleFitness(testExecutor.start(sourceCode).getSuccessRate());
  }

}
