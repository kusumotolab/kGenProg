package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，1つ目の親の選択アルゴリズムを表すインターフェース．
 *
 * @author higo
 */
public interface FirstVariantSelectionStrategy {

  /**
   * 選択を行うメソッド．選択対象の個体群を引数として与える必要あり．
   *
   * @param variants 選択対象の個体群
   * @return 選択された個体
   */
  Variant exec(List<Variant> variants);

  /**
   * kGenProgが基本実装として持つ，1つ目の親を選択するアルゴリズムを表す列挙型．
   *
   * TODO ここに定義すべきではないものな気がするので将来移動させる予定．
   *
   * @author higo
   */
  public enum Strategy {

    /**
     * 1つ目の親を評価関数に基づいて選択するアルゴリズムを表す型
     *
     * @see jp.kusumotolab.kgenprog.ga.crossover.FirstVariantEliteSelection
     */
    Elite {
      /**
       * 1つ目の親の選択を行うインスタンスを生成するメソッド．
       *
       * @see FirstVariantSelectionStrategy.Strategy#initialize(Random)
       *
       * @param random 選択においてランダム処理を行うためのシード
       * @return 1つ目の親の選択を行うインスタンス
       */
      @Override
      public FirstVariantSelectionStrategy initialize(final Random random) {
        return new FirstVariantEliteSelection(random);
      }
    },

    /**
     * 1つ目の親をランダム選択するアルゴリズムを表す型
     *
     * @see jp.kusumotolab.kgenprog.ga.crossover.FirstVariantRandomSelection
     */
    Random {
      /**
       * 1つ目の親の選択を行うインスタンスを生成するメソッド．
       *
       * @see FirstVariantSelectionStrategy.Strategy#initialize(Random)
       *
       * @param random 選択においてランダム処理を行うためのシード
       * @return 1つ目の親の選択を行うインスタンス
       */
      @Override
      public FirstVariantSelectionStrategy initialize(final Random random) {
        return new FirstVariantRandomSelection(random);
      }
    };

    /**
     * 1つ目の親の選択を行うインスタンスを生成するための抽象メソッド．
     *
     * @param random 選択においてランダム処理を行うためのシード
     * @return 1つ目の親の選択を行うインスタンス
     */
    public abstract FirstVariantSelectionStrategy initialize(final Random random);
  }
}
