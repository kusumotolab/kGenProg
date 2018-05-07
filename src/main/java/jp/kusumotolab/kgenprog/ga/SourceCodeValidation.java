package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestProcessBuilder;

public interface SourceCodeValidation {

    public Fitness exec(GeneratedSourceCode sourceCode, TargetProject project, TestProcessBuilder testExecutor);
}
