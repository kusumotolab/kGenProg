package jp.kusumotolab.kgenprog.ga.validation;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * ソースコードを個体としてどれくらい優秀か評価するインターフェース
 */
public interface SourceCodeValidation {

  public class Input {

    private final GeneratedSourceCode sourceCode;
    private final TestResults testResults;

    public Input(final GeneratedSourceCode sourceCode, final TestResults testResults) {
      this.sourceCode = sourceCode;
      this.testResults = testResults;
    }

    
    public GeneratedSourceCode getSourceCode() {
      return sourceCode;
    }

    
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
