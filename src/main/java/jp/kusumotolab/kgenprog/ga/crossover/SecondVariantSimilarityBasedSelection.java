package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親を1つ目の親との何らかの類似度に基づいて選択するアルゴリズムを実装したクラス．<br>
 * なお，類似度が低い方が優先的に選択されることに注意．<br>
 *
 * @author higo
 */
public abstract class SecondVariantSimilarityBasedSelection
    implements SecondVariantSelectionStrategy {

  private final Random random;

  /**
   * コンストラクタ．選択においてランダム処理を行うためのシードを引数として渡す必要あり．
   *
   * @param random ランダム処理を行うためのシード
   */
  protected SecondVariantSimilarityBasedSelection(final Random random) {
    this.random = random;
  }

  /**
   * 選択を行うメソッド．選択対象の個体群および1つ目の親として選択された個体をを引数として与える必要あり．
   *
   * @param variants 選択対象の個体群
   * @param firstVariant 1つ目の親として選択された個体
   * @return 選択された個体
   * @see jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy#exec(List, Variant)
   */
  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant)
      throws CrossoverInfeasibleException {

    double minSimilarity = 1.0d;

    // secondVariantの初期値を，一つ目の親以外のバリアントからランダムに選択
    final List<Variant> secondVariantCandidates = variants.stream()
        .filter(v -> !v.equals(firstVariant))
        .collect(Collectors.toList());
    if (secondVariantCandidates.isEmpty()) { // 候補リストが空の時は例外を投げる
      throw new CrossoverInfeasibleException("no variant for second parent");
    }
    Variant secondVariant =
        secondVariantCandidates.get(random.nextInt(secondVariantCandidates.size()));

    for (final Variant variant : variants) {
      final double similarity = calculateSimilarity(firstVariant, variant);
      if (similarity < minSimilarity) {
        minSimilarity = similarity;
        secondVariant = variant;
      }
    }

    return secondVariant;
  }

  protected abstract double calculateSimilarity(final Variant variant1, final Variant variant2);
}
