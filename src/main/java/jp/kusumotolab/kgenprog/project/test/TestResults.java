package jp.kusumotolab.kgenprog.project.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.project.Location;

public class TestResults implements Serializable {

  private static final long serialVersionUID = 1L;

  // 直接valueへのアクセスを回避するために可視性を下げておく
  private final Map<FullyQualifiedName, TestResult> value;

  public TestResults() {
    value = new HashMap<>();
  }

  /**
   * 新規TestResultの追加
   * 
   * @param testResult
   */
  public void add(final TestResult testResult) {
    this.value.put(testResult.executedTestFQN, testResult);
  }

  /**
   * 別のTestResultsをまとめて追加する
   * 
   * @param testResults
   */
  @Deprecated
  public void addAll(final TestResults testResults) {
    testResults.value.forEach(this.value::putIfAbsent);
  }

  /**
   * 失敗したテストのFQN一覧を取得．
   * 
   * @return 失敗したテスト結果s
   */
  public List<TestResult> getFailedTestResults() {
    return this.value.values().stream().filter(r -> r.failed).collect(Collectors.toList());
  }

  /**
   * 成功したテストのFQN一覧を取得．
   * 
   * @return 成功したテスト結果s
   */
  public List<TestResult> getSuccessedTestResults() {
    return this.value.values().stream().filter(r -> !r.failed).collect(Collectors.toList());
  }

  /**
   * obsoleted
   * 
   * @return
   */
  @Deprecated
  public List<TestResult> getTestResults() {
    return value.values().stream().collect(Collectors.toList());
  }

  /**
   * 実行されたテストメソッドのFQN一覧を返す．
   * 
   * @return
   */
  public Set<FullyQualifiedName> getExecutedTestFQNs() {
    return this.value.keySet();
  }

  /**
   * 実行された単一テストメソッドの結果を返す．
   * 
   * @param testFQN 対象のテストメソッドFQN
   * @return
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
    final int success = getSuccessedTestResults().size();
    return 1.0 * success / (success + fail);
  }

  /**
   * FLで用いる4メトリクスのprivateなParameterized-Method
   * 
   * @param targetFQN 計算対象クラスのFQN
   * @param lineNumber 計算対象クラスの行番号
   * @param status 実行されたか否か
   * @param failed テストの成否
   * @return
   */
  private List<FullyQualifiedName> getTestFQNs(final FullyQualifiedName targetFQN,
      final int lineNumber, final Coverage.Status status, final boolean failed) {
    final List<FullyQualifiedName> result = new ArrayList<>();
    for (final TestResult testResult : this.value.values()) {
      final Coverage coverage = testResult.getCoverages(targetFQN);
      final Coverage.Status _status = coverage.statuses.get(lineNumber - 1);
      if (status == _status && failed == testResult.failed) {
        result.add(testResult.executedTestFQN);
      }
    }
    return result;
  }

  /**
   * a_ef
   * 
   * @param targetFQN ターゲットクラスのFQN
   * @param lineNumber ターゲットクラスの行番号
   * @return a_ef
   */
  public List<FullyQualifiedName> getFailedTestFQNsExecutingTheStatement(
      final FullyQualifiedName targetFQN, final int lineNumber) {
    return getTestFQNs(targetFQN, lineNumber, Coverage.Status.COVERED, true);
  }

  /**
   * a_ep
   * 
   * @param targetFQN ターゲットクラスのFQN
   * @param lineNumber ターゲットクラスの行番号
   * @return
   */
  public List<FullyQualifiedName> getPassedTestFQNsExecutingTheStatement(
      final FullyQualifiedName targetFQN, final int lineNumber) {
    return getTestFQNs(targetFQN, lineNumber, Coverage.Status.COVERED, false);
  }

  /**
   * a_nf
   * 
   * @param targetFQN ターゲットクラスのFQN
   * @param lineNumber ターゲットクラスの行番号
   * @return
   */
  public List<FullyQualifiedName> getFailedTestFQNsNotExecutingTheStatement(
      final FullyQualifiedName targetFQN, final int lineNumber) {
    return getTestFQNs(targetFQN, lineNumber, Coverage.Status.NOT_COVERED, true);
  }

  /**
   * a_np
   * 
   * @param targetFQN ターゲットクラスのFQN
   * @param lineNumber ターゲットクラスの行番号
   * @return
   */
  public List<FullyQualifiedName> getPassedTestFQNsNotExecutingTheStatement(
      final FullyQualifiedName targetFQN, final int lineNumber) {
    return getTestFQNs(targetFQN, lineNumber, Coverage.Status.NOT_COVERED, false);
  }

  @Deprecated
  public Map<Location, Integer> getExecutedFailedTestCounts() {
    return null;
  }

  @Deprecated
  public Map<Location, Integer> getNotExecutedFailedTestCounts() {
    return null;
  }

  @Deprecated
  public Map<Location, Integer> getExecutedPassedTestCounts() {
    return null;
  }

  @Deprecated
  public Map<Location, Integer> getNotExecutedPassedTestCounts() {
    return null;
  }

  /**
   * serialize()とdeserialize()で用いる.serファイルパス． 注意：固定名で処理しているので，並列処理は不可能．
   * 
   * @return
   */
  public static Path getSerFilePath() {
    return Paths.get(System.getProperty("java.io.tmpdir") + "/kgenprog-testresults.ser");
  }

  /**
   * ファイルシステム上の.serへのserializer． 注意：固定名で処理しているので，並列処理は不可能．
   * 
   * @param testResults serialize対象のオブジェクト
   */
  public static void serialize(TestResults testResults) {
    try {
      getSerFilePath().toFile().delete();
      getSerFilePath().toFile().createNewFile();
      final ObjectOutputStream out =
          new ObjectOutputStream(Files.newOutputStream(getSerFilePath()));
      out.writeObject(testResults);
      out.close();
    } catch (FileNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  /**
   * ファイルシステム上の.serからのdeserializer() 注意：固定名で処理しているので，並列処理は不可能．
   * 
   * @return deserialize後のオブジェクト
   */
  public static TestResults deserialize() {
    try {
      final ObjectInputStream in = new ObjectInputStream(Files.newInputStream(getSerFilePath()));
      final TestResults testResults = (TestResults) in.readObject();
      in.close();
      return testResults;
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    sb.append(String.join(",\n",
        this.value.values().stream().map(v -> v.toString(2)).collect(Collectors.toList())));
    sb.append("\n");
    sb.append("]\n");
    return sb.toString();
  }
}
