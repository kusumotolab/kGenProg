package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，1つ目の親をランダムに選択するアルゴリズムを実装したクラス．
 * 
 * @author higo
 *
 */
public class FirstVariantRandomSelection implements FirstVariantSelectionStrategy {

  private final Random random;

  /**
   * コンストラクタ．選択においてランダム処理を行うためのシード値を引数として渡す必要あり．
   * 
   * @param random ランダム処理を行うためのシード
   */
  public FirstVariantRandomSelection(final Random random) {
    this.random = random;
  }

  @Override
  public Variant exec(final List<Variant> variants) {
    return variants.get(random.nextInt(variants.size()));
  }
}
