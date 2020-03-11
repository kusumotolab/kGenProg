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

  @Deprecated
  public EmptyBuildResults() {
    super(null, null, null, true);
  }

  public EmptyBuildResults(final DiagnosticCollector<JavaFileObject> diagnostics,
      final String buildProgressText) {
    super(null, diagnostics, buildProgressText, true);
  }
}
