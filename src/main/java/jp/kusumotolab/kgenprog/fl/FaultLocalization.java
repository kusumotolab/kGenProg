package jp.kusumotolab.kgenprog.fl;

import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public interface FaultLocalization {

  public void exec(TargetProject targetProject, Variant variant, TestExecutor testExecutor);
}
