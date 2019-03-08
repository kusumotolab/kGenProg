package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;

public final class BuildConfigPath extends SourcePath {

  /**
   * pathをrootPathからの相対パスに変換してからBuildConfigPathを生成します
   *
   * @param rootPath プロジェクトルートディレクトリへのパス
   * @param path ビルドツールの設定ファイルへのパス（絶対パスもしくはカレントディレクトリからの相対パス）
   * @return BuildConfigPath
   */
  public static BuildConfigPath relativizeAndCreate(final Path rootPath, final Path path) {
    return new BuildConfigPath(rootPath, SourcePath.relativize(rootPath, path));
  }

  public BuildConfigPath(final Path rootPath, final Path path) {
    super(rootPath, path);
  }

  @Override
  public FullyQualifiedName createFullyQualifiedName(final String className) {
    return new TargetFullyQualifiedName(className);
  }
}
