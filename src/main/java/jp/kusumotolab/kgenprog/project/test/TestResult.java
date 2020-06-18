package jp.kusumotolab.kgenprog.project.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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

  @Override
  public String toString() {
    return toString(0);
  }

  /**
   * jsonシリアライザ
   *
   * @param indentDepth インデント幅
   * @return
   */
  public String toString(final int indentDepth) {
    final StringBuilder sb = new StringBuilder();
    final String indent = StringUtils.repeat(" ", indentDepth);
    sb.append(indent + "{\n");
    sb.append(indent + "  \"executedTestFQN\": \"" + executedTestFQN + "\",\n");
    sb.append(indent + "  \"wasFailed\": " + failed + ",\n");
    sb.append(indent + "  \"coverages\": [\n");
    sb.append(String.join(",\n", coverages.values()
        .stream()
        .map(c -> c.toString(indentDepth + 2))
        .collect(Collectors.toList())));
    sb.append("\n");
    sb.append(indent + "  ]\n");
    sb.append(indent + "}");
    return sb.toString();
  }
}
