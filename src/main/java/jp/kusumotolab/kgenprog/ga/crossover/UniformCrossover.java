package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.UniformCrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 一様交叉を行うクラス．
 *
 * @author higo
 */
public class UniformCrossover extends CrossoverAdaptor {

  private final Random random;

  /**
   * コンストラクタ．一様交叉に必要な情報を全て引数として渡す必要あり．
   *
   * @param random 交叉処理の内部でランダム処理を行うためのシード値
   * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
   * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
   * @param generatingCount 一世代の交叉処理で生成する個体の数
   * @param requiredSolutions 生成する必要がある修正プログラムの数
   * @return 交叉を行うインスタンス
   */
  public UniformCrossover(final Random random,
      final FirstVariantSelectionStrategy firstVariantSelectionStrategy,
      final SecondVariantSelectionStrategy secondVariantSelectionStrategy,
      final int generatingCount, final int requiredSolutions) {
    super(firstVariantSelectionStrategy, secondVariantSelectionStrategy, generatingCount,
        requiredSolutions);
    this.random = random;
  }

  @Override
  protected List<Variant> filter(final List<Variant> variants) {
    return variants.stream()
        .filter(e -> 1 < e.getGene() // 遺伝子の長さが2に満たないバリアントは交叉に使えない
            .getBases()
            .size())
        .collect(Collectors.toList());
  }

  @Override
  protected List<Variant> makeVariants(final List<Variant> variants, final VariantStore store)
      throws CrossoverInfeasibleException {
    final Variant variantA = getFirstVariantSelectionStrategy().exec(variants);
    final Variant variantB = getSecondVariantSelectionStrategy().exec(variants, variantA);
    final Gene geneA = variantA.getGene();
    final Gene geneB = variantB.getGene();
    final List<Base> basesA = geneA.getBases();
    final List<Base> basesB = geneB.getBases();

    final Gene newGene = makeGene(basesA, basesB);
    final HistoricalElement newElement = new UniformCrossoverHistoricalElement(variantA, variantB);
    return Arrays.asList(store.createVariant(newGene, newElement));
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {
    final List<Base> bases = new ArrayList<>();
    for (int i = 0; i < Math.max(basesA.size(), basesB.size()); i++) {

      // iがbasesAのサイズよりも大きい場合は，
      // basesBからBaseを取得するかどうかをランダムで決める
      if (basesA.size() <= i) {
        if (this.random.nextBoolean()) {
          bases.add(basesB.get(i));
        }
        continue;
      }

      // iがbasesBのサイズよりも大きい場合は，
      // basesAからBaseを取得するかどうかをランダムで決める
      if (basesB.size() <= i) {
        if (this.random.nextBoolean()) {
          bases.add(basesA.get(i));
        }
        continue;
      }

      // iがbaseAとbaseBのどちらのサイズよりも小さい場合は，
      // どちらのBaseを取得するかランダムに決める
      bases.add(this.random.nextBoolean() ? basesA.get(i) : basesB.get(i));
    }
    return new Gene(bases);
  }
}
