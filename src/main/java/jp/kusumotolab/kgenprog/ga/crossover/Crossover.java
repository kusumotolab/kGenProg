package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 交叉を表すインターフェース． 交叉アルゴリズムを実装するクラスはこのインターフェースを実装しなければならない．
 * 
 * @author higo
 *
 */
public interface Crossover {

  /**
   * 交叉処理を行うメソッド．交叉対象の個体群を含んだVariantStoreを引数として与える必要あり．
   * 
   * @param variantStore 交叉対象の個体群
   * @return 交叉により生成された個体群
   */
  List<Variant> exec(VariantStore variantStore);

  /**
   * 1つ目の親を返す．
   * 
   * @return 1つ目の親
   */
  FirstVariantSelectionStrategy getFirstVariantSelectionStrategy();

  /**
   * 2つ目の親を返す．
   * 
   * @return 2つ目の親
   */
  SecondVariantSelectionStrategy getSecondVariantSelectionStrategy();

  /**
   * kGenProgが基本実装として持つ交叉種別を表す列挙型．
   * 
   * TODO ここに定義すべきではないものな気がするので将来移動させる予定．
   * 
   * @author higo
   *
   */
  enum Type {
    Random {

      /**
       * 交叉を行うインスタンスを生成するメソッド．
       * 
       * @see Crossover.Type#initialize(Random, FirstVariantSelectionStrategy,
       *      SecondVariantSelectionStrategy, int)
       * 
       * @param random 交叉処理の内部でランダム処理を行うためのシード
       * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
       * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
       * @param generatingCount 一世代の交叉処理で生成する個体の数
       * @return 交叉を行うインスタンス
       */
      @Override
      public Crossover initialize(final Random random,
          final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
          final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
          final int generatingCount) {
        return new RandomCrossover(random, firstVariantSelectionStrategy,
            secondVariantSelectionStrategy, generatingCount);
      }
    },

    SinglePoint {

      /**
       * 交叉を行うインスタンスを生成するメソッド．
       * 
       * @see Crossover.Type#initialize(Random, FirstVariantSelectionStrategy,
       *      SecondVariantSelectionStrategy, int)
       * 
       * @param random 交叉処理の内部でランダム処理を行うためのシード
       * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
       * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
       * @param generatingCount 一世代の交叉処理で生成する個体の数
       * @return 交叉を行うインスタンス
       */
      @Override
      public Crossover initialize(final Random random,
          final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
          final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
          final int generatingCount) {
        return new SinglePointCrossover(random, firstVariantSelectionStrategy,
            secondVariantSelectionStrategy, generatingCount);
      }
    },

    Uniform {

      /**
       * 交叉を行うインスタンスを生成するメソッド．
       * 
       * @see Crossover.Type#initialize(Random, FirstVariantSelectionStrategy,
       *      SecondVariantSelectionStrategy, int)
       * 
       * @param random 交叉処理の内部でランダム処理を行うためのシード
       * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
       * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
       * @param generatingCount 一世代の交叉処理で生成する個体の数
       * @return 交叉を行うインスタンス
       */
      @Override
      public Crossover initialize(final Random random,
          final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
          final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
          final int generatingCount) {
        return new UniformCrossover(random, firstVariantSelectionStrategy,
            secondVariantSelectionStrategy, generatingCount);
      }
    };

    /**
     * 交叉を行うインスタンスを生成するための抽象メソッド．
     * 
     * @param random 交叉処理の内部でランダム処理を行うためのシード
     * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
     * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
     * @param generatingCount 一世代の交叉処理で生成する個体の数
     * @return 交叉を行うインスタンス
     */
    public abstract Crossover initialize(final Random random,
        final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
        final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
        final int generatingCount);
  }
}
