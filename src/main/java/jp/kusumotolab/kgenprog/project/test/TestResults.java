package jp.kusumotolab.kgenprog.project.test;

import static java.util.stream.Collectors.toList;
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
import jp.kusumotolab.kgenprog.project.BuildResults;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.Range;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class TestResults implements Serializable {

  private static final long serialVersionUID = 1L;

  // 直接valueへのアクセスを回避するために可視性を下げておく
  private final Map<FullyQualifiedName, TestResult> value;

  // 再利用可能な空TestResultsインスタンス．
  // TODO immutabilityが確保できていないので作用する可能性がある．
  // issue #79
  public static final TestResults EMPTY_VALUE = new TestResults();

  TestResults() {
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
    return this.value.values().stream().filter(r -> r.failed).collect(toList());
  }

  /**
   * 成功したテストのFQN一覧を取得．
   * 
   * @return 成功したテスト結果s
   */
  public List<TestResult> getSuccessedTestResults() {
    return this.value.values().stream().filter(r -> !r.failed).collect(toList());
  }

  /**
   * obsoleted
   *
   * @return
   */
  @Deprecated
  public List<TestResult> getTestResults() {
    return value.values().stream().collect(toList());
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

    // TODO 一時的な実装．
    // 全テストが失敗した時（コンパイル失敗時等）に，successRateはどうあるべきか？新たな型を切るほうがよさそう
    if (success + fail == 0) {
      return Double.NaN;
    }
    return 1.0 * success / (success + fail);
  }

  /**
   * a_ef
   *
   * @param targetFQN ターゲットクラスのFQN
   * @param lineNumber ターゲットクラスの行番号
   * @return a_ef
   */
  @Deprecated
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
  @Deprecated
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
  @Deprecated
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
  @Deprecated
  public List<FullyQualifiedName> getPassedTestFQNsNotExecutingTheStatement(
      final FullyQualifiedName targetFQN, final int lineNumber) {
    return getTestFQNs(targetFQN, lineNumber, Coverage.Status.NOT_COVERED, false);
  }

  /**
   * FLで用いる4メトリクスのprivateなParameterized-Method
   * 
   * @param sourceFile
   * @param location
   * @param status
   * @param failed
   * @return
   */
  private long getNumberOfTests(final SourceFile sourceFile, final Location location,
      final Coverage.Status status, final boolean failed) {

    // 翻訳1: SourceFile → [FQN]
    final Set<FullyQualifiedName> correspondingFqns =
        this.buildResults.getPathToFQNs(sourceFile.path);

    if (null == correspondingFqns) {
      return 0;
    }
    // 翻訳2: location → 行番号
    // TODO
    // GeneratedSourceCode#inferLineNumbers(Location) を使うか Location#inferLineNumbers()を使うか．
    // 後者の方が嫉妬の度合いが低そう
    // final Range correspondingRange = this.buildResults.sourceCode.inferLineNumbers(location);
    final Range correspondingRange = location.inferLineNumbers();

    // TODO location:lineNum = 1:N の時の対策が必要．ひとまずNの一行目だけを使う．
    final int correspondingLineNumber = correspondingRange.start;

    return correspondingFqns.stream()
        .map(fqn -> getTestFQNs(fqn, correspondingLineNumber, status, failed))
        .flatMap(v -> v.stream()).count();
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
    for (final TestResult testResult : this.value.values()) {
      final Coverage coverage = testResult.getCoverages(targetFQN);
      if (null != coverage) {
        final Coverage.Status _status = coverage.statuses.get(lineNumber - 1);
        if (status == _status && failed == testResult.failed) {
          result.add(testResult.executedTestFQN);
        }
      }
    }
    return result;
  }

  /**
   * a_ep
   * 
   * @param sourceFile
   * @param location
   * @return
   */
  public long getNumberOfPassedTestsExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return getNumberOfTests(sourceFile, location, Coverage.Status.COVERED, false);
  }

  /**
   * a_ef
   * 
   * @param sourceFile
   * @param location
   * @return
   */
  public long getNumberOfFailedTestsExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return getNumberOfTests(sourceFile, location, Coverage.Status.COVERED, true);
  }

  /**
   * a_np
   * 
   * @param sourceFile
   * @param location
   * @return
   */
  public long getNumberOfPassedTestsNotExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return getNumberOfTests(sourceFile, location, Coverage.Status.NOT_COVERED, false);
  }

  /**
   * a_nf
   * 
   * @param sourceFile
   * @param location
   * @return
   */
  public long getNumberOfFailedTestsNotExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return getNumberOfTests(sourceFile, location, Coverage.Status.NOT_COVERED, true);
  }

  /**
   * serialize()とdeserialize()で用いる.serファイルパス． 注意：固定名で処理しているので，並列処理は不可能．
   * 
   * @return
   */
  public static Path getSerFilePath() {
    return Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-testresults.ser");
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
  public static TestResults deserialize() throws IOException, ClassNotFoundException {
    final ObjectInputStream in = new ObjectInputStream(Files.newInputStream(getSerFilePath()));
    final TestResults testResults = (TestResults) in.readObject();
    in.close();
    return testResults;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    sb.append(
        String.join(",\n", this.value.values().stream().map(v -> v.toString(2)).collect(toList())));
    sb.append("\n");
    sb.append("]\n");
    return sb.toString();
  }

  /*
   * 以降，翻訳のための一時的な処理
   */

  // 翻訳用ASTを持つbuildResults
  private BuildResults buildResults;

  public void setBuildResults(final BuildResults buildResults) {
    this.buildResults = buildResults;
  }

}
