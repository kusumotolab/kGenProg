package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public interface SourceCodeValidation {

  public void exec(Variant variant, TargetProject project, TestExecutor testExecutor);
}
