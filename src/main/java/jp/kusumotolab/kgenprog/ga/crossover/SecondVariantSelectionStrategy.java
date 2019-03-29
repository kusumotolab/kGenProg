package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親の選択アルゴリズムを表すインターフェース． 一つ目の親として選択された個体とは異なる個体を選択する必要がある．
 * 
 * @author higo
 *
 */
public interface SecondVariantSelectionStrategy {

  /**
   * 選択を行うメソッド．選択対象の個体群および1つ目の親として選択された個体をを引数として与える必要あり．
   * 
   * @param variants 選択対象の個体群
   * @param firstVariant 1つ目の親として選択された個体
   * @return 選択された個体
   */
  Variant exec(List<Variant> variants, Variant firstVariant) throws CrossoverInfeasibleException;

  public enum Strategy {
    Elite {

      @Override
      public SecondVariantSelectionStrategy initialize(final Random random) {
        return new SecondVariantEliteSelection();
      }
    },

    GeneSimilarity {

      @Override
      public SecondVariantSelectionStrategy initialize(final Random random) {
        return new SecondVariantGeneSimilarityBasedSelection(random);
      }
    },

    Random {

      @Override
      public SecondVariantSelectionStrategy initialize(final java.util.Random random) {
        return new SecondVariantRandomSelection(random);
      }
    },

    TestComplementary {

      @Override
      public SecondVariantSelectionStrategy initialize(final Random random) {
        return new SecondVariantTestComplementaryBasedSelection();
      }
    };

    public abstract SecondVariantSelectionStrategy initialize(final Random random);
  }
}
