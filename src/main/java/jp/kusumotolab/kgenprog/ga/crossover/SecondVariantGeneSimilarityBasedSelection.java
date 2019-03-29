package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親を1つ目の親との遺伝子の違いに基づいて選択するアルゴリズムを実装したクラス．
 * 
 * @author higo
 *
 */
public class SecondVariantGeneSimilarityBasedSelection
    extends SecondVariantSimilarityBasedSelection {

  /**
   * コンストラクタ．選択においてランダム処理を行うためのシードを引数として渡す必要あり．
   * 
   * @param random ランダム処理を行うためのシード
   */
  public SecondVariantGeneSimilarityBasedSelection(final Random random) {
    super(random);
  }

  @Override
  protected double calculateSimilarity(final Variant variant1, final Variant variant2) {
    return SimilarityCalculator.exec(variant1, variant2, v -> v.getGene()
        .getBases());
  }
}
