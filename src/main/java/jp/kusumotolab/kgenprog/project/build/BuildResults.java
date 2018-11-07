package jp.kusumotolab.kgenprog.project.build;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class BuildResults {

  private final BinaryStore binaryStore;

  public final boolean isBuildFailed;

  // TODO コンパイルできないときのエラー情報はほんとにこの型でいいか？
  public final DiagnosticCollector<JavaFileObject> diagnostics;

  // ビルド実行時のテキスト出力
  public final String buildProgressText;

  /**
   * コンストラクタ（後で書き換え TODO）
   * 
   * @param sourceCode ビルド元となったソースコード
   * @param diagnostics ビルド時の詳細情報
   * @param buildProgressText ビルド実行時のテキスト出力
   */
  protected BuildResults(final BinaryStore binaryStore,
      final DiagnosticCollector<JavaFileObject> diagnostics, final String buildProgressText,
      final boolean isBuildFailed) {
    this.binaryStore = binaryStore;
    this.diagnostics = diagnostics;
    this.buildProgressText = buildProgressText;
    this.isBuildFailed = isBuildFailed;
  }

  public BinaryStore getBinaryStore() {
    return binaryStore;
  }

}
