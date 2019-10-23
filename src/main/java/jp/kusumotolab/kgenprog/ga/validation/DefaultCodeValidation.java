package jp.kusumotolab.kgenprog.ga.validation;

/**
 * テスト通過率をそのまま評価値とする評価方法
 */
public class DefaultCodeValidation implements SourceCodeValidation {

  /**
   * @param input 評価値計算に利用する情報
   * @return 評価値
   */
  @Override
  public Fitness exec(final Input input) {
    return new SimpleFitness(input.getTestResults()
        .getSuccessRate());
  }
}
