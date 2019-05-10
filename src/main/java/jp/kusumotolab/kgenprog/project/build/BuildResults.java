package jp.kusumotolab.kgenprog.project.build;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * ビルド結果を表すオブジェクト．<br>
 * ビルドの成否やビルド時の診断情報，ビルド結果たるバイナリ格納庫を保持する．<br>
 * 
 * @author shinsuke
 *
 */
public class BuildResults {

  /**
   * ビルドの結果得られたバイナリの集合
   */
  public final BinaryStore binaryStore;

  /**
   * ビルドの成否
   */
  public final boolean isBuildFailed;

  /**
   * ビルド時の診断情報
   */
  public final DiagnosticCollector<JavaFileObject> diagnostics;

  /**
   * ビルド実行時のテキスト出力
   */
  public final String buildProgressText;

  /**
   * コンストラクタ．各種ビルド結果を保持する．
   * 
   * @param binaryStore ビルドの結果得られたバイナリの格納庫
   * @param diagnostics ビルド時の診断情報
   * @param buildProgressText ビルド実行時のテキスト出力
   * @param isBuildFailed ビルドの成否
   */
  protected BuildResults(final BinaryStore binaryStore,
      final DiagnosticCollector<JavaFileObject> diagnostics, final String buildProgressText,
      final boolean isBuildFailed) {
    this.binaryStore = binaryStore;
    this.diagnostics = diagnostics;
    this.buildProgressText = buildProgressText;
    this.isBuildFailed = isBuildFailed;
  }

}
