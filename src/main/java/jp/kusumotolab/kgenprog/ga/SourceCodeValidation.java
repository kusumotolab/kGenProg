package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.test.TestResults;

public interface SourceCodeValidation {

  public Fitness exec(VariantStore variantStore, TestResults testResults);
}
