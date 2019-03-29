package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 交叉において，2つ目の親を1つ目の親とのテストの相補性に基づいて選択するアルゴリズムを実装したクラス． 選択の第一基準：1つ目の親が失敗しているテストを多く成功している．
 * 選択の第二基準：1つ目の親が成功しているテストを多く成功している．
 * 
 * @author higo
 *
 */
public class SecondVariantTestComplementaryBasedSelection
    implements SecondVariantSelectionStrategy {


  /**
   * 選択を行うメソッド．選択対象の個体群および1つ目の親として選択された個体をを引数として与える必要あり．
   *
   * @see jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy#exec(List, Variant)
   * 
   * @param variants 選択対象の個体群
   * @param firstVariant 1つ目の親として選択された個体
   * @return 選択された個体
   */
  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant)
      throws CrossoverInfeasibleException {

    // 処理手順は以下の通り．
    // 1. 1つ目の親を取り除いた個体群（secondVariantCandidates）を作成
    // 2. secondVariantCandidatesを，1つ目の親が成功したテストについてテストの成功数の降順でソート．
    // 3. secondVariantCandidatesを，1つ目の親が失敗したテストについてテストの成功数の降順でソート．
    // 4. secondVariantCandidatesの先頭の個体を返す．

    // 1つ目の親を取り除いた個体群のリストを作成
    final List<Variant> secondVariantCandidates = variants.stream()
        .filter(v -> !v.equals(firstVariant))
        .collect(Collectors.toList());
    if (secondVariantCandidates.isEmpty()) { // 候補リストが空の時は例外を投げる
      throw new CrossoverInfeasibleException("no variant for second parent");
    }

    // firstVariantにおいて，失敗したテスト一覧(failedTestFQNs)と成功したテスト一覧(successedTestFQNs)を取得
    final TestResults testResults = firstVariant.getTestResults();
    final List<FullyQualifiedName> failedTestFQNs = testResults.getFailedTestFQNs();
    final List<FullyQualifiedName> successedTestFQNs = testResults.getSuccessedTestFQNs();

    // secondVariantCandidatesを，successedTestFQNsにおいて成功したテストが多い順にソートし，
    // そのあとにfailedTestFQNsにおいて成功したテストが多い順にソート
    final Comparator<Variant> comparator = Comparator
        .<Variant>comparingLong(v -> getSuccessedNumber(v.getTestResults(), successedTestFQNs))
        .reversed()
        .thenComparingLong(v -> getSuccessedNumber(v.getTestResults(), failedTestFQNs))
        .reversed();
    Collections.sort(secondVariantCandidates, comparator);

    // リストの最初の要素を返す
    return secondVariantCandidates.get(0);
  }

  private Long getSuccessedNumber(final TestResults testResults,
      final Collection<FullyQualifiedName> targetFQNs) {
    return targetFQNs.stream()
        .filter(fqn -> !testResults.getTestResult(fqn).failed)
        .count();
  }
}
