package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.time.StopWatch;

import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.RandomCrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * ランダム交叉を行うクラス．
 *
 * @author higo
 */
public class RandomCrossover extends CrossoverAdaptor {

  private final Random random;

  /**
   * コンストラクタ．ランダム交叉に必要な情報を全て引数として渡す必要あり．
   *
   * @param random 交叉処理の内部でランダム処理を行うためのシード
   * @param firstVariantSelectionStrategy 1つ目の親を選ぶためのアルゴリズム
   * @param secondVariantSelectionStrategy 2つ目の親を選ぶためのアルゴリズム
   * @param generatingCount 一世代の交叉処理で生成する個体の数
   * @param requiredSolutions 生成する必要がある修正プログラムの数
   * @return 交叉を行うインスタンス
   */
  public RandomCrossover(final Random random,
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
    final StopWatch stopWatch = StopWatch.createStarted();
    final Variant variantA = getFirstVariantSelectionStrategy().exec(variants);
    final Variant variantB = getSecondVariantSelectionStrategy().exec(variants, variantA);
    final Gene geneA = variantA.getGene();
    final Gene geneB = variantB.getGene();
    final List<Base> basesA = geneA.getBases();
    final List<Base> basesB = geneB.getBases();

    final Gene newGene = makeGene(basesA, basesB);
    final HistoricalElement newElement = new RandomCrossoverHistoricalElement(variantA, variantB);
    //return Arrays.asList(store.createVariant(newGene, newElement));
    return Arrays.asList(store.createVariant(newGene, newElement, stopWatch.getTime()));
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {

    final List<Base> concatenatedBases = Stream.concat(basesA.stream(), basesB.stream())
        .collect(Collectors.toList());
    final List<Base> bases = new ArrayList<>();

    for (int i = 0; true; i++) {

      // basesAとbasesBの短い方よりもiが小さいときは，
      // Baseをランダムに1つ選択
      if (i < Math.min(basesA.size(), basesB.size())) {
        final int selectedIndex = random.nextInt(concatenatedBases.size());
        final Base selectedBase = concatenatedBases.remove(selectedIndex);
        bases.add(selectedBase);
        continue;
      }

      // basesAとbasesBの長い方よりもiが小さいときは，
      // Baseをランダムに1つ選択するかどうかランダムに決める
      if (i < Math.max(basesA.size(), basesB.size())) {
        if (random.nextBoolean()) {
          final int selectedIndex = random.nextInt(concatenatedBases.size());
          final Base selectedBase = concatenatedBases.remove(selectedIndex);
          bases.add(selectedBase);
        }
        continue;
      }

      // basesAとbasesBの長い方よりもiが大きくなってしまったらループを抜ける
      break;
    }

    return new Gene(bases);
  }
}
