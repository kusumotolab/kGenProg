package jp.kusumotolab.kgenprog.ga.validation;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * テスト通過率をそのまま評価値とする評価方法
 */
public class DefaultCodeValidation implements SourceCodeValidation {

  /**
   * @param sourceCode 評価するソースコード
   * @param testResults ソースコードのテストの結果
   * @return 評価値
   */
  @Override
  public Fitness exec(final GeneratedSourceCode sourceCode, final TestResults testResults) {
    return new SimpleFitness(testResults.getSuccessRate());
  }
}
