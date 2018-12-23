package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Random;

/**
 * バリアントの類似度に基づき一点交叉を行うクラス． 一つ目のバリアントはランダムに選び，二つ目のバリアントは一つ目と最もGeneの類似度が低いものを選らぶ．
 * 
 */
public class SimilarityBasedSinglePointCrossover extends SinglePointCrossover {

  public SimilarityBasedSinglePointCrossover(final Random random,
      final int crossoverGeneratingCount) {
    super(random, crossoverGeneratingCount);
  }

  @Override
  public Variant selectSecondVariant(final List<Variant> variants, final Variant firstVariant) {

    final Gene firstGene = firstVariant.getGene();
    double minSimilarity = 1.0d;
    Variant secondVariant = null;

    for (final Variant variant : variants) {
      final Gene gene = variant.getGene();
      final double similarity = Gene.getSimilarity(firstGene, gene);
      if (similarity < minSimilarity) {
        minSimilarity = similarity;
        secondVariant = variant;
      }
    }

    return secondVariant;
  }
}
