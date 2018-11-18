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
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.JavaBinaryObject;

public class TestResults implements Serializable {

  private static final long serialVersionUID = 1L;

  // 直接valueへのアクセスを回避するために可視性を下げておく
  private final Map<FullyQualifiedName, TestResult> value;

  TestResults() {
    this.value = new HashMap<>();
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
  public List<TestResult> getSuccessedTestResults() {
    return this.value.values()
        .stream()
        .filter(r -> !r.failed)
        .collect(Collectors.toList());
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
   * @param productSourcePath
   * @param location
   * @param status
   * @param failed
   * @return
   */
  private long getNumberOfTests(final ProductSourcePath productSourcePath,
      final ASTLocation location, final Coverage.Status status, final boolean failed) {

    // 翻訳1: SourcePath → [FQN]
    // 翻訳1: SourcePath → [FQN]
    final Set<FullyQualifiedName> correspondingFqns = buildResults.getBinaryStore()
        .get(productSourcePath)
        .stream()
        .map(JavaBinaryObject::getFqn)
        .collect(Collectors.toSet());

    // 翻訳2: location → 行番号
    // TODO
    // GeneratedSourceCode#inferLineNumbers(Location) を使うか Location#inferLineNumbers()を使うか．
    // 後者の方が嫉妬の度合いが低そう
    // final Range correspondingRange = this.buildResults.sourceCode.inferLineNumbers(location);
    final LineNumberRange correspondingRange = location.inferLineNumbers();

    // TODO location:lineNum = 1:N の時の対策が必要．ひとまずNの一行目だけを使う．
    final int correspondingLineNumber = correspondingRange.start;

    return correspondingFqns.stream()
        .map(fqn -> getTestFQNs(fqn, correspondingLineNumber, status, failed))
        .flatMap(v -> v.stream())
        .count();
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

      if (coverage == null || lineNumber > coverage.statuses.size()) {
        // 計測対象（targetFQN）の行の外を参照した場合．
        // （＝内部クラス等の理由で，その行の実行結果が別テストのcoverageに記述されている場合）
        // 何もしなくて良い．
        // その行の結果は別のcoverageインスタンスに保存されているため．
        continue;
      }
      final Coverage.Status _status = coverage.statuses.get(lineNumber - 1);
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
    return getNumberOfTests(productSourcePath, location, Coverage.Status.COVERED, false);
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
    return getNumberOfTests(productSourcePath, location, Coverage.Status.COVERED, true);
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
    return getNumberOfTests(productSourcePath, location, Coverage.Status.NOT_COVERED, false);
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
    return getNumberOfTests(productSourcePath, location, Coverage.Status.NOT_COVERED, true);
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
      Files.deleteIfExists(getSerFilePath());
      Files.createFile(getSerFilePath());
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
    sb.append(String.join(",\n", this.value.values()
        .stream()
        .map(v -> v.toString(2))
        .collect(Collectors.toList())));
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
