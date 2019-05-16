package jp.kusumotolab.kgenprog.ga.validation;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * ソースコードを個体としてどれくらい優秀か評価するインターフェース
 */
public interface SourceCodeValidation {

  /**
   * @param sourceCode 評価するソースコード
   * @param testResults ソースコードのテストの結果
   * @return 評価値
   */
  Fitness exec(GeneratedSourceCode sourceCode, TestResults testResults);
}
