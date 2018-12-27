package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * テスト結果の類似度に基づき一点交叉を行うクラス． 一つ目のバリアントはランダムに選び，二つ目のバリアントは一つ目とテスト結果が最も異なるものを選ぶ．
 * 
 */
public class TestBasedSinglePointCrossover extends SinglePointCrossover {

  public TestBasedSinglePointCrossover(final Random random, final int crossoverGeneratingCount) {
    super(random, crossoverGeneratingCount);
  }

  @Override
  public Variant selectSecondVariant(final List<Variant> variants, final Variant firstVariant) {

    final TestResults firstTestResults = firstVariant.getTestResults();
    double minSimilarity = 1.0d;
    Variant secondVariant = firstVariant;

    for (final Variant variant : variants) {
      final TestResults testResults = variant.getTestResults();
      final double similarity = TestResults.getSimilarity(firstTestResults, testResults);
      if (similarity < minSimilarity) {
        minSimilarity = similarity;
        secondVariant = variant;
      }
    }

    return secondVariant;
  }
}
