package jp.kusumotolab.kgenprog.project.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * 単一のテスト結果を表すオブジェクト．<br>
 * テストの成否とカバレッジ情報を持つ．<br>
 *
 * @author shinsuke
 */
public class TestResult {

  public final FullyQualifiedName executedTestFQN;
  public final boolean failed;
  private final String failedReason;
  private final Map<FullyQualifiedName, Coverage> coverages;

  /**
   * constructor
   *
   * @param executedTestFQN 実行したテストメソッドの名前
   * @param failed テストの結果
   * @param failedReason テストに落ちた場合はその理由
   * @param coverages テスト対象それぞれの行ごとのCoverage計測結果
   */
  public TestResult(final FullyQualifiedName executedTestFQN, final boolean failed,
      final String failedReason, final Map<FullyQualifiedName, Coverage> coverages) {
    this.executedTestFQN = executedTestFQN;
    this.failed = failed;
    this.failedReason = failedReason;
    this.coverages = coverages;
  }

  /**
   * 実行されたテストのFQN一覧を取得
   *
   * @return 実行されたテストのFQN一覧
   */
  public List<FullyQualifiedName> getExecutedTargetFQNs() {
    return this.coverages.entrySet()
        .stream()
        .map(Entry::getKey)
        .collect(Collectors.toList());
  }

  /**
   * 指定テストFQNに対するカバレッジの結果を取得
   *
   * @param testFQN カバレッジの結果
   * @return
   */
  public Coverage getCoverages(final FullyQualifiedName testFQN) {
    return this.coverages.get(testFQN);
  }

  /**
   * failedReasonを取得
   *
   * @return
   */
  public String getFailedReason() {
    return this.failedReason;
  }
}
