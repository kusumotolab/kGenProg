package jp.kusumotolab.kgenprog.project.build;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * ビルド失敗時を表すBuildResultsオブジェクト．<br>
 * いわゆるNullオブジェクト．
 *
 * @author shinsuke
 */
public class FailedBuildResults extends BuildResults {

  /**
   * @deprecated
   */
  @Deprecated
  public FailedBuildResults() {
    super(null, null, null, true);
  }

  public FailedBuildResults(final DiagnosticCollector<JavaFileObject> diagnostics,
      final String buildProgressText) {
    super(null, diagnostics, buildProgressText, true);
  }
}
