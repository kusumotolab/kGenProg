package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public interface SourceCodeValidation {

  Fitness exec(GeneratedSourceCode sourceCode, TestResults testResults);
}
