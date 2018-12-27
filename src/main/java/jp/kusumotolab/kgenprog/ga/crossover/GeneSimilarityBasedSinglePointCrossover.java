package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * バリアントの類似度に基づき一点交叉を行うクラス． 一つ目のバリアントはランダムに選び，二つ目のバリアントは一つ目と最もGeneの類似度が低いものを選らぶ．
 * 
 */
public class GeneSimilarityBasedSinglePointCrossover extends SimilarityBasedSinglePointCrossover {

  public GeneSimilarityBasedSinglePointCrossover(final Random random,
      final int crossoverGeneratingCount) {
    super(random, crossoverGeneratingCount);
  }

  @Override
  protected double calculateSimilarity(final Variant variant1, final Variant variant2) {
    return SimilarityCalculator.exec(variant1, variant2, v -> v.getGene()
        .getBases());
  }
}
