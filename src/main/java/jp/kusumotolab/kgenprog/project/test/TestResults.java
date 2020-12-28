package jp.kusumotolab.kgenprog.project.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.JavaBinaryObject;
import jp.kusumotolab.kgenprog.project.test.Coverage.Status;

/**
 * 全テストの結果を表すオブジェクト．<br>
 * TestResultの集合．<br>
 *
 * FL計算に用いる情報集約APIを持つ．<br>
 *
 * @author shinsuke
 */
public class TestResults {

  private BuildResults buildResults;

  private final Map<FullyQualifiedName, TestResult> value;

  private final double testExecTime;

  /**
   * constructor
   */
  public TestResults() {
    this.value = new HashMap<>();
    this.testExecTime = Double.NaN;
  }

  /**
   * constructor
   */
  public TestResults(final BuildResults buildResults) {
    this.value = new HashMap<>();
    this.buildResults = buildResults;
    this.testExecTime = Double.NaN;
  }

  /**
   * constructor
   */
  public TestResults(final BuildResults buildResults, final double testExecTime,
      final List<TestResult> testResultList) {
    this.value = new HashMap<>();
    testResultList.forEach(testResult -> this.value.put(testResult.executedTestFQN, testResult));
    this.buildResults = buildResults;
    this.testExecTime = testExecTime;
  }

  /**
   * 失敗したテストのFQN一覧を取得．
   *
   * @return 失敗したテスト結果s
   */
  public List<TestResult> getFailedTestResults() {
    return this.value.values()
        .stream()
        .filter(r -> r.failed)
        .collect(Collectors.toList());
  }

  /**
   * 成功したテストのFQN一覧を取得．
   *
   * @return 成功したテスト結果s
   */
  public List<TestResult> getSucceededTestResults() {
    return this.value.values()
        .stream()
        .filter(r -> !r.failed)
        .collect(Collectors.toList());
  }

  /**
   * 実行されたテストメソッドのFQN一覧を返す．
   *
   * @return 実行されたテストメソッドのFQN一覧
   */
  public Set<FullyQualifiedName> getExecutedTestFQNs() {
    return this.value.keySet();
  }

  /**
   * 失敗したテストのFQN一覧を取得．
   *
   * @return 失敗したテストのFQN一覧
   */
  public List<FullyQualifiedName> getFailedTestFQNs() {
    return this.value.values()
        .stream()
        .filter(r -> r.failed)
        .map(r -> r.executedTestFQN)
        .collect(Collectors.toList());
  }

  /**
   * 成功したテストのFQN一覧を取得．
   *
   * @return 成功したテストのFQN一覧
   */
  public List<FullyQualifiedName> getSucceededTestFQNs() {
    return this.value.values()
        .stream()
        .filter(r -> !r.failed)
        .map(r -> r.executedTestFQN)
        .collect(Collectors.toList());
  }

  /**
   * 実行された単一テストメソッドの結果を返す．
   *
   * @param testFQN 対象のテストメソッドFQN
   * @return 実行された単一テストメソッドの結果
   */
  public TestResult getTestResult(final FullyQualifiedName testFQN) {
    // TODO if null
    return this.value.get(testFQN);
  }

  /**
   * テスト成功率を取得（全成功テストメソッド / 全実行テストメソッド）
   *
   * @return テスト成功率
   */
  public double getSuccessRate() {
    final int fail = getFailedTestResults().size();
    final int success = getSucceededTestResults().size();

    return 1.0 * success / (success + fail);
  }

  /**
   * FLで用いる4メトリクスのprivateなParameterized-Method
   *
   * @param productSourcePath
   * @param location
   * @param status
   * @param failed
   * @return
   */
  private long getNumberOfTests(final ProductSourcePath productSourcePath,
      final ASTLocation location, final boolean failed) {

    // 翻訳1: SourcePath → [FQN]
    final Set<FullyQualifiedName> correspondingFqns = getCorrespondingFqns(productSourcePath);

    // 翻訳2: location → 行番号
    // TODO
    // GeneratedSourceCode#inferLineNumbers(Location) を使うか Location#inferLineNumbers()を使うか．
    // 後者の方が嫉妬の度合いが低そう
    // final Range correspondingRange = this.buildResults.sourceCode.inferLineNumbers(location);
    final LineNumberRange correspondingRange = location.inferLineNumbers();

    // TODO location:lineNum = 1:N の時の対策が必要．ひとまずNの一行目だけを使う．
    final int correspondingLineNumber = correspondingRange.start;

    return correspondingFqns.stream()
        .map(fqn -> getTestFQNs(fqn, correspondingLineNumber, Status.COVERED, failed))
        .mapToLong(Collection::size)
        .sum();
  }

  /**
   * FLで用いる4メトリクスのprivateなParameterized-Method
   *
   * @param targetFQN 計算対象クラスのFQN
   * @param lineNumber 計算対象クラスの行番号
   * @param status 実行されたか否か
   * @param failed テストが失敗したかどうか
   * @return
   */
  private List<FullyQualifiedName> getTestFQNs(final FullyQualifiedName targetFQN,
      final int lineNumber, final Coverage.Status status, final boolean failed) {
    final List<FullyQualifiedName> result = new ArrayList<>();

    // 全てのテストケースを探索
    for (final TestResult testResult : this.value.values()) {
      final Coverage coverage = testResult.getCoverages(targetFQN);

      if (coverage == null || lineNumber > coverage.getStatusesSize()) {
        // 計測対象（targetFQN）の行の外を参照した場合．
        // （＝内部クラス等の理由で，その行の実行結果が別テストのcoverageに記述されている場合）
        // 何もしなくて良い．
        // その行の結果は別のcoverageインスタンスに保存されているため．
        continue;
      }
      final Coverage.Status _status = coverage.getStatus(lineNumber - 1);
      if (status == _status && failed == testResult.failed) {
        result.add(testResult.executedTestFQN);
      }
    }
    return result;
  }

  /**
   * a_ep
   *
   * @param productSourcePath
   * @param location
   * @return
   */
  public long getNumberOfPassedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return getNumberOfTests(productSourcePath, location, false);
  }

  /**
   * a_ef
   *
   * @param productSourcePath
   * @param location
   * @return
   */
  public long getNumberOfFailedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return getNumberOfTests(productSourcePath, location, true);
  }

  /**
   * a_np
   *
   * @param productSourcePath
   * @param location
   * @return
   */
  public long getNumberOfPassedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return getSucceededTestResults().size()
        - getNumberOfPassedTestsExecutingTheStatement(productSourcePath, location);
  }

  /**
   * a_nf
   *
   * @param productSourcePath
   * @param location
   * @return
   */
  public long getNumberOfFailedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return getFailedTestResults().size()
        - getNumberOfFailedTestsExecutingTheStatement(productSourcePath, location);
  }

  /**
   * jsonシリアライザ
   *
   * @return
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    sb.append(String.join(",\n", this.value.values()
        .stream()
        .map(v -> v.toString(2))
        .collect(Collectors.toList())));
    sb.append("\n");
    sb.append("]\n");
    return sb.toString();
  }

  /**
   * ビルドの結果 (分散に使用)
   *
   * @return
   */
  public BuildResults getBuildResults() {
    return buildResults;
  }

  /**
   * Pathに対応するFQNを返す (分散に使用するため，privateにはしない)
   *
   * @return
   */
  protected Set<FullyQualifiedName> getCorrespondingFqns(
      final ProductSourcePath productSourcePath) {
    return buildResults.binaryStore.get(productSourcePath)
        .stream()
        .map(JavaBinaryObject::getFqn)
        .collect(Collectors.toSet());
  }

  /**
   * テスト実行時間
   *
   * @return
   */
  public double getTestTime() {
    return this.testExecTime;
  }
}
