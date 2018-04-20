package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class DefaultCodeValidation implements SourceCodeValidation {

	@Override
	public Fitness exec(GeneratedSourceCode sourceCode, TargetProject project, TestExecutor testExecutor) {
		return new SimpleFitness(testExecutor.exec(sourceCode).getSuccessRate());
	}

}
