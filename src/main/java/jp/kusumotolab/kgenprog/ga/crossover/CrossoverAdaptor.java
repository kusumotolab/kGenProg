package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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

  private static Logger log = LoggerFactory.getLogger(CrossoverAdaptor.class);

  private final FirstVariantSelectionStrategy firstVariantSelectionStrategy;
  private final SecondVariantSelectionStrategy secondVariantSelectionStrategy;
  private final int generatingCount;

  /**
   * コンストラクタ．交叉に必要な情報を全て引数として渡す必要あり．
   *
   * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
   * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
   * @param generatingCount 一世代の交叉処理で生成する個体の数
   * @return 交叉を行うインスタンス
   */
  public CrossoverAdaptor(final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
      final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
      final int generatingCount) {
    this.firstVariantSelectionStrategy = firstVariantSelectionStrategy;
    this.secondVariantSelectionStrategy = secondVariantSelectionStrategy;
    this.generatingCount = generatingCount;
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
  final public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> filteredVariants = variantStore.getCurrentVariants()
        .stream()
        .filter(e -> 1 < e.getGene() // 遺伝子の長さが2に満たないバリアントは交叉に使えない
            .getBases()
            .size())
        .collect(Collectors.toList());

    // filteredVariantsの要素数が2に満たない場合は交叉しない
    if (filteredVariants.size() < 2) {
      return Collections.emptyList();
    }

    final List<Variant> variants = new ArrayList<>();
    try {
      // generatingCountを超えるまでバリアントを作りづづける
      while (variants.size() < generatingCount) {
        final List<Variant> newVariants = makeVariants(filteredVariants, variantStore);
        variants.addAll(newVariants);
      }
    } catch (final CrossoverInfeasibleException e) {
      log.debug(e.getMessage());
    }

    // バリアントを作りすぎた場合はそれを除いてリターン
    return variants.subList(0, generatingCount);
  }

  protected abstract List<Variant> makeVariants(List<Variant> variants, VariantStore variantStore)
      throws CrossoverInfeasibleException;
}
