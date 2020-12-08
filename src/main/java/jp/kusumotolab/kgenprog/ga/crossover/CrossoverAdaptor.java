package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 交叉のアダプタークラス．<br>
 * 交叉アルゴリズムを実装するクラスはインターフェースCrossoverを直接実装する代わりにこのクラスを継承してもよい．<br>
 * このクラスを継承した方が，コードの記述量が少なくなるはず．<br>
 *
 * @author higo
 */
public abstract class CrossoverAdaptor implements Crossover {

  private static final Logger log = LoggerFactory.getLogger(CrossoverAdaptor.class);

  private final FirstVariantSelectionStrategy firstVariantSelectionStrategy;
  private final SecondVariantSelectionStrategy secondVariantSelectionStrategy;
  private final int generatingCount;
  private final int requiredSolutions;

  /**
   * コンストラクタ．交叉に必要な情報を全て引数として渡す必要あり．
   *
   * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
   * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
   * @param generatingCount 一世代の交叉処理で生成する個体の数
   * @param requiredSolutions 生成する必要がある修正プログラムの数
   */
  public CrossoverAdaptor(final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
      final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
      final int generatingCount, final int requiredSolutions) {
    this.firstVariantSelectionStrategy = firstVariantSelectionStrategy;
    this.secondVariantSelectionStrategy = secondVariantSelectionStrategy;
    this.generatingCount = generatingCount;
    this.requiredSolutions = requiredSolutions;
  }

  /**
   * 1つ目の親を返す．
   *
   * @return 1つ目の親
   * @see jp.kusumotolab.kgenprog.ga.crossover.Crossover#getFirstVariantSelectionStrategy()
   */
  @Override
  public FirstVariantSelectionStrategy getFirstVariantSelectionStrategy() {
    return firstVariantSelectionStrategy;
  }

  /**
   * 2つ目の親を返す．
   *
   * @return 2つ目の親
   * @see jp.kusumotolab.kgenprog.ga.crossover.Crossover#getSecondVariantSelectionStrategy()
   */
  @Override
  public SecondVariantSelectionStrategy getSecondVariantSelectionStrategy() {
    return secondVariantSelectionStrategy;
  }

  /**
   * 交叉処理を行うメソッド．交叉対象の個体群を含んだVariantStoreを引数として与える必要あり．
   *
   * @param variantStore 交叉対象の個体群
   * @return 交叉により生成された個体群
   */
  @Override
  public List<Variant> exec(final VariantStore variantStore) {

    int foundSolutions = variantStore.getFoundSolutionsNumber()
        .get();

    // すでに必要な数の修正プログラムがある場合は何もせずにこのメソッドを抜ける
    if (requiredSolutions <= foundSolutions) {
      return Collections.emptyList();
    }

    final List<Variant> validVariants = filter(variantStore.getCurrentVariants());

    // filteredVariantsの要素数が2に満たない場合は交叉しない
    if (validVariants.size() < 2) {
      return Collections.emptyList();
    }

    final List<Variant> variants = generateVariants(variantStore, validVariants);
    return variants;
  }

  private List<Variant> generateVariants(final VariantStore variantStore,
      final List<Variant> validVariants) {

    final List<Variant> generatedVariants = new ArrayList<>();
    int foundSolutions = variantStore.getFoundSolutionsNumber()
        .get();

    while (generatedVariants.size() < generatingCount) {
      try {
        final List<Variant> newVariants = makeVariants(validVariants, variantStore);

        // 新しい修正プログラムが生成された場合，必要数に達しているかを調べる
        // 達している場合はそこで処理を終える
        for (final Variant newVariant : newVariants) {
          generatedVariants.add(newVariant);
          if (newVariant.isCompleted()) {
            foundSolutions++;
          }
          if (requiredSolutions <= foundSolutions) {
            return generatedVariants;
          }
        }
      } catch (final CrossoverInfeasibleException e) {
        log.debug(e.getMessage());
      }
    }
    return generatedVariants;
  }

  protected abstract List<Variant> makeVariants(List<Variant> variants, VariantStore variantStore)
      throws CrossoverInfeasibleException;

  protected abstract List<Variant> filter(List<Variant> variants);
}
