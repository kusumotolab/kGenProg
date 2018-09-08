package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public interface SourceCodeValidation {

  public Fitness exec(GeneratedSourceCode sourceCode, TargetProject project,
      TestExecutor testExecutor);
}
