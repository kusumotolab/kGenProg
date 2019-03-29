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

  List<Variant> exec(VariantStore variantStore);

  FirstVariantSelectionStrategy getFirstVariantSelectionStrategy();

  SecondVariantSelectionStrategy getSecondVariantSelectionStrategy();

  enum Type {
    Random {

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
     * 交叉を行うインスタンスを生成するための抽象メソッド．交叉アルゴリズムを実装するクラスはこのメソッドを実装しなければならない．
     * 
     * @param random 交叉処理の内部でランダム処理を行うためのシード値
     * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
     * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
     * @param generatingCount 一度の交叉処理で生成する個体の数
     * @return 交叉を行うインスタンス
     */
    public abstract Crossover initialize(final Random random,
        final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
        final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
        final int generatingCount);
  }
}
