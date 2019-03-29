package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親をランダムに選択するアルゴリズムを実装したクラス．
 * 
 * @author higo
 *
 */
public class SecondVariantRandomSelection implements SecondVariantSelectionStrategy {

  private final Random random;

  /**
   * コンストラクタ．ランダム選択を行うためのシードを引数として渡す必要あり．．
   * 
   * @param random ランダム選択を行うためのシード
   */
  public SecondVariantRandomSelection(final Random random) {
    this.random = random;
  }

  /**
   * 選択を行うメソッド．選択対象の個体群および1つ目の親として選択された個体をを引数として与える必要あり．
   *
   * @see jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy#exec(List, Variant)
   * 
   * @param variants 選択対象の個体群
   * @param firstVariant 1つ目の親として選択された個体
   * @return 選択された個体
   */
  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant)
      throws CrossoverInfeasibleException {

    final List<Variant> secondVariantCandidates = variants.stream()
        .filter(v -> !v.equals(firstVariant))
        .collect(Collectors.toList());
    if (secondVariantCandidates.isEmpty()) { // 候補リストが空の時は例外を投げる
      throw new CrossoverInfeasibleException("no variant for second parent");
    }
    return secondVariantCandidates.get(random.nextInt(secondVariantCandidates.size()));
  }
}
