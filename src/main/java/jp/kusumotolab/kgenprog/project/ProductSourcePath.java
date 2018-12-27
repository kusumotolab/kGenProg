package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;

public final class ProductSourcePath extends SourcePath {

  /**
   * pathをrootPathからの相対パスに変換してからProductSourcePathを生成します
   * 
   * @param rootPath プロジェクトルートディレクトリへのパス
   * @param path ソースコードへのパス（絶対パスもしくはカレントディレクトリからの相対パス）
   * @return ProductSourcePath
   */
  public static ProductSourcePath relativizeAndCreate(final Path rootPath, final Path path) {
    return new ProductSourcePath(rootPath, SourcePath.relativize(rootPath, path));
  }

  public ProductSourcePath(final Path rootPath, final Path path) {
    super(rootPath, path);
  }

  @Override
  public FullyQualifiedName createFullyQualifiedName(final String className) {
    return new TargetFullyQualifiedName(className);
  }
}
