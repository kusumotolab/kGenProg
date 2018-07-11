package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class EmptyTestResults extends TestResults {

  private static final long serialVersionUID = 1L;

  public static final EmptyTestResults instance = new EmptyTestResults();

  private EmptyTestResults() {}

  @Override
  public double getSuccessRate() {
    return Double.NaN;
  }

  @Override
  public long getNumberOfPassedTestsExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return 0;
  }

  @Override
  public long getNumberOfFailedTestsExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return 0;
  }

  @Override
  public long getNumberOfPassedTestsNotExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return 0;
  }

  @Override
  public long getNumberOfFailedTestsNotExecutingTheStatement(final SourceFile sourceFile,
      final Location location) {
    return 0;
  }

}
