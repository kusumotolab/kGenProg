package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class SecondVariantTestSimilarityBasedSelection implements SecondVariantSelectionStrategy {

  // 処理手順は以下の通り．
  // 1. 第一バリアントを取り除いたバリアントのリスト（secondVariantCandidates）を作成
  // 2. secondVariantCandidatesを，第一バリアントが成功したテストについてテストの成功数の降順でソート．
  // 3. secondVariantCandidatesを，第一バリアントが失敗したテストについてテストの成功数の降順でソート．
  // 4. secondVariantCandidatesの先頭の要素を返す．
  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant)
      throws CrossoverInfeasibleException {

    // 第一バリアントを取り除いたバリアントのリストを作成
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

    // variantsの最初の要素を返す
    return secondVariantCandidates.get(0);
  }

  private Long getSuccessedNumber(final TestResults testResults,
      final Collection<FullyQualifiedName> targetFQNs) {
    return new Long(targetFQNs.stream()
        .filter(fqn -> !testResults.getTestResult(fqn).failed)
        .count());
  }
}
