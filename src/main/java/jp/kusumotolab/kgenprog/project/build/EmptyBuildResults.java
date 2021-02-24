package jp.kusumotolab.kgenprog.project.build;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * ビルド失敗時を表すBuildResultsオブジェクト．<br>
 * いわゆるNullオブジェクト．
 *
 * @author shinsuke
 */
public class EmptyBuildResults extends BuildResults {

  /**
   * @deprecated
   */
  @Deprecated
  public EmptyBuildResults() {
    super(null, null, null, true, Double.NaN);
  }

  public EmptyBuildResults(final DiagnosticCollector<JavaFileObject> diagnostics,
      final String buildProgressText, final double buildTime) {
    super(null, diagnostics, buildProgressText, true, buildTime);
  }
}
