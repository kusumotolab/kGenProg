package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，1つ目の親の選択アルゴリズムを表すインターフェース．
 * 
 * @author higo
 *
 */
public interface FirstVariantSelectionStrategy {

  /**
   * 選択を行うメソッド．選択対象の個体群を引数として与える必要あり．
   * 
   * @param variants 選択対象の個体群
   * @return 選択された個体
   */
  Variant exec(List<Variant> variants);

  public enum Strategy {
    Elite {

      @Override
      public FirstVariantSelectionStrategy initialize(final Random random) {
        return new FirstVariantEliteSelection(random);
      }
    },

    Random {

      @Override
      public FirstVariantSelectionStrategy initialize(final Random random) {
        return new FirstVariantRandomSelection(random);
      }
    };

    public abstract FirstVariantSelectionStrategy initialize(final Random random);
  }
}
