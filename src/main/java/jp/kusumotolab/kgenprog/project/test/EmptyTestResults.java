package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class EmptyTestResults extends TestResults {

  public static final EmptyTestResults instance = new EmptyTestResults();

  private EmptyTestResults() {}

  @Override
  public double getSuccessRate() {
    return Double.NaN;
  }

  @Override
  public long getNumberOfPassedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return 0;
  }

  @Override
  public long getNumberOfFailedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return 0;
  }

  @Override
  public long getNumberOfPassedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return 0;
  }

  @Override
  public long getNumberOfFailedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return 0;
  }

}
