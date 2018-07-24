package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class EmptyTestResults extends TestResults {

  private static final long serialVersionUID = 1L;

  public static final EmptyTestResults instance = new EmptyTestResults();

  private EmptyTestResults() {}

  @Override
  public double getSuccessRate() {
    return Double.NaN;
  }

  @Override
  public long getNumberOfPassedTestsExecutingTheStatement(final SourcePath sourcePath,
      final ASTLocation location) {
    return 0;
  }

  @Override
  public long getNumberOfFailedTestsExecutingTheStatement(final SourcePath sourcePath,
      final ASTLocation location) {
    return 0;
  }

  @Override
  public long getNumberOfPassedTestsNotExecutingTheStatement(final SourcePath sourcePath,
      final ASTLocation location) {
    return 0;
  }

  @Override
  public long getNumberOfFailedTestsNotExecutingTheStatement(final SourcePath sourcePath,
      final ASTLocation location) {
    return 0;
  }

}
