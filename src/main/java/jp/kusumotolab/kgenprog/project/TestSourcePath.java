package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;

public class TestSourcePath extends SourcePath {

  /**
   * pathをrootPathからの相対パスに変換してからTestSourcePathを生成します
   * 
   * @param rootPath プロジェクトルートディレクトリへのパス
   * @param path ソースコードへのパス（絶対パスもしくはカレントディレクトリからの相対パス）
   * @return TestSourcePath
   */
  public static TestSourcePath relativizeAndCreate(final Path rootPath, final Path path) {
    return new TestSourcePath(rootPath, SourcePath.relativize(rootPath, path));
  }

  public TestSourcePath(final Path rootPath, final Path path) {
    super(rootPath, path);
  }

  @Override
  public FullyQualifiedName createFullyQualifiedName(final String className) {
    return new TestFullyQualifiedName(className);
  }
}
