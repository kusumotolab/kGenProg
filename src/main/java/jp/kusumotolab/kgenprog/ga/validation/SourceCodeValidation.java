package jp.kusumotolab.kgenprog.ga.validation;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * ソースコードを個体としてどれくらい優秀か評価するインターフェース
 */
public interface SourceCodeValidation {

  /**
   * SourceCOdeValidation#exec用のパラメータオブジェクト
   *
   */
  public class Input {

    private final GeneratedSourceCode sourceCode;
    private final TestResults testResults;

    /**
     * 
     * @param sourceCode 評価対象のソースコード
     * @param testResults 評価対象のテスト結果
     */
    public Input(final GeneratedSourceCode sourceCode, final TestResults testResults) {
      this.sourceCode = sourceCode;
      this.testResults = testResults;
    }

    /**
     * 
     * @return 評価対象のソースコード
     */
    public GeneratedSourceCode getSourceCode() {
      return sourceCode;
    }

    /**
     * 
     * @return 評価対象のテスト結果
     */
    public TestResults getTestResults() {
      return testResults;
    }
  }

  /**
   * @param sourceCode 評価するソースコード
   * @param testResults ソースコードのテストの結果
   * @return 評価値
   */
  Fitness exec(Input input);
}
