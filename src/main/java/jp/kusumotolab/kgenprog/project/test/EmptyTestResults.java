package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.build.BuildResults;

/**
 * テスト失敗時のテスト結果．<br>
 * いわゆるNullオブジェクト．<br>
 *
 * @author shinsuke
 */
public class EmptyTestResults extends TestResults {

  private final String cause;

  public EmptyTestResults(final BuildResults buildResults) {
    super(buildResults);
    this.cause = buildResults.diagnostics.getDiagnostics()
        .stream()
        .map(d -> d.getMessage(null))
        .reduce("", String::concat);
  }

  public EmptyTestResults(final String cause) {
    this.cause = cause;
  }

  /**
   * {@inheritDoc}<br>
   * Double.NaNを返す
   */
  @Override
  public double getSuccessRate() {
    return Double.NaN;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfPassedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return 0;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfFailedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return 0;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfPassedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return 0;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfFailedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return 0;
  }

  /**
   * テスト結果が得られなかった理由を返す．
   *
   * @return テスト結果が得られなかった理由
   */
  public String getCause() {
    return cause;
  }

  /**
   * {@inheritDoc}<br>
   * Double.NaNを返す
   */
  @Override
  public double getTestTime() {
    return Double.NaN;
  }
}
