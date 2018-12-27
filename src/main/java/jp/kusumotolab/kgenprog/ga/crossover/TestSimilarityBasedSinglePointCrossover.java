package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * テスト結果の類似度に基づき一点交叉を行うクラス． 一つ目のバリアントはランダムに選び，二つ目のバリアントは一つ目とテスト結果が最も異なるものを選ぶ．
 * 
 */
public class TestSimilarityBasedSinglePointCrossover extends SimilarityBasedSinglePointCrossover {

  public TestSimilarityBasedSinglePointCrossover(final Random random,
      final int crossoverGeneratingCount) {
    super(random, crossoverGeneratingCount);
  }

  @Override
  protected double calculateSimilarity(final Variant variant1, final Variant variant2) {
    return SimilarityCalculator.exec(variant1, variant2, v -> v.getTestResults()
        .getFailedTestFQNs());
  }
}
