package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Comparator;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親を評価関数に基づいて選択するアルゴリズムを実装したクラス．
 * 
 * @author higo
 *
 */
public class SecondVariantEliteSelection implements SecondVariantSelectionStrategy {

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
    return variants.stream()
        .sorted(Comparator.comparing(Variant::getFitness)
            .reversed())
        .filter(v -> v != firstVariant) // TODO 本来は Variantにequalsメソッドを定義すべき？
        .findFirst()
        .orElseThrow(() -> new CrossoverInfeasibleException("no variant for second parent"));
  }
}
