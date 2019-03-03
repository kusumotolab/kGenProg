package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉の二つ目の親を選択する戦略のインターフェース． 一つ目の親として選択されたバリアントとは異なるバリアントを選択する必要がある．
 * 
 * @author higo
 *
 */
public interface SecondVariantSelectionStrategy {

  /**
   * 交叉の二つ目の親を選択するメソッド．
   * 
   * @param variants 親の候補
   * @param firstVariant 一つ目の親として選択されたバリアント
   * @return 二つ目の親として選択されたバリアント
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
