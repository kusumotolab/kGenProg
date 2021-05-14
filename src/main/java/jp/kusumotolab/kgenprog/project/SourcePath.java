package jp.kusumotolab.kgenprog.project;

import java.nio.file.Path;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public abstract class SourcePath {

  private final Path resolvedPath;

  /**
   * {@link TargetProject#rootPath} からの相対パス
   */
  public final Path path;

  /**
   * SourcePathを生成する
   *
   * @param rootPath プロジェクトルートへのパス {@link TargetProject#rootPath}
   * @param path ルートからの相対パス
   */
  protected SourcePath(final Path rootPath, final Path path) {
    this.resolvedPath = rootPath.resolve(path);
    this.path = path;
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof SourcePath)) {
      return false;
    }
    return path.equals(((SourcePath) o).path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return this.path.toString();
  }

  public Path getResolvedPath() {
    return resolvedPath;
  }

  public abstract FullyQualifiedName createFullyQualifiedName(String className);

  /**
   * 相対パスに変換する
   *
   * @param base 基準となるパス
   * @param target 対象パス
   * @return 相対パス
   */
  public static Path relativize(final Path base, final Path target) {
    return base.normalize()
        .toAbsolutePath()
        .relativize(target.normalize()
            .toAbsolutePath());
  }
}
