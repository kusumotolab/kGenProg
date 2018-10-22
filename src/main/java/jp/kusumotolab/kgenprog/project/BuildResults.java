package jp.kusumotolab.kgenprog.project;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import jp.kusumotolab.kgenprog.project.build.BinaryStore;

public class BuildResults {

  public final boolean isBuildFailed;

  // TODO コンパイルできないときのエラー情報はほんとにこの型でいいか？
  public final DiagnosticCollector<JavaFileObject> diagnostics;

  // ビルド実行時のテキスト出力
  public final String buildProgressText;

  // ビルド元となったソースコード
  public final GeneratedSourceCode sourceCode;


  /**
   * コンストラクタ（後で書き換え TODO）
   * 
   * @param sourceCode ビルド元となったソースコード
   * @param isBuildFailed ビルドの成否
   * @param diagnostics ビルド時の詳細情報
   * @param buildProgressText ビルド実行時のテキスト出力
   */
  protected BuildResults(final GeneratedSourceCode sourceCode, final boolean isBuildFailed,
      final DiagnosticCollector<JavaFileObject> diagnostics, final String buildProgressText,
      final BinaryStore binaryStore) {
    this.sourceCode = sourceCode;
    this.isBuildFailed = isBuildFailed;
    this.diagnostics = diagnostics;
    this.buildProgressText = buildProgressText;
    this.binaryStore = binaryStore;
  }

  final BinaryStore binaryStore;

  public BinaryStore getBinaryStore() {
    return binaryStore;
  }

}
