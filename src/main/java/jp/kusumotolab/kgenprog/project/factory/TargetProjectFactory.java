package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class TargetProjectFactory {

  /**
   * TargetProjectを生成するファクトリメソッド． 各種ビルドツールの設定ファイルが存在すればそこから，
   * 存在しなければヒューリスティックにフォルダを探索してTargetProjectを生成する．
   * 
   * @param rootPath 対象のルートパス
   * @return TargetProject
   */
  public static TargetProject create(final Path rootPath) {
    if (!Files.exists(rootPath)) {
      throw new IllegalArgumentException("Specified project does not exist: \"" + rootPath + "\".");
    }

    final ProjectFactory applicableFactory = instanceProjectFactories(rootPath).stream()
        .filter(ProjectFactory::isApplicable)
        .findFirst()
        .orElse(new HeuristicProjectFactory(rootPath));

    return applicableFactory.create();
  }

  // /**
  // * @see TargetProjectFactory#create
  // * @param rootPath
  // * @return
  // */
  // @Deprecated
  // public static TargetProject create(final String rootPath) {
  // return create(Paths.get(rootPath));
  // }

  /**
   * TargetProjectを生成するファクトリメソッド． 全パラメータを指定する必要あり．
   * 
   * @param rootPath 対象のルートパス
   * @param pathsForProductSource
   * @param pathsForTestSource
   * @param pathsForClass
   * @param junitVersion
   * @return TargetProject
   */
  public static TargetProject create(final Path rootPath, final List<Path> pathsForProductSource,
      final List<Path> pathsForTestSource, final List<Path> pathsForClass,
      final JUnitVersion junitVersion) {
    final List<Path> pathsForBuildConfig = getBuildConfigPaths(rootPath);
    return new DefaultProjectFactory(rootPath, pathsForProductSource, pathsForTestSource,
        pathsForClass, junitVersion, pathsForBuildConfig).create();
  }

  /**
   * ファクトリ一覧の生成
   * 
   * @param rootPath
   * @return
   */
  private static List<ProjectFactory> instanceProjectFactories(final Path rootPath) {
    return Arrays.asList(new AntProjectFactory(rootPath), new MavenProjectFactory(rootPath),
        new GradleProjectFactory(rootPath));
  }

  /**
   * ファクトリ一覧の生成（BuildToolProject）
   *
   * @param rootPath
   * @return
   */
  private static List<BuildToolProjectFactory> instanceProjectFactoriesInBuildTool(
      final Path rootPath) {
    return Arrays.asList(new AntProjectFactory(rootPath), new MavenProjectFactory(rootPath),
        new GradleProjectFactory(rootPath));
  }

  /**
   * ビルドツールの設定ファイルへのパスを得る 入手不可なときはnullを返す
   *
   * @param rootPath
   * @return
   */
  private static List<Path> getBuildConfigPaths(final Path rootPath) {
    final BuildToolProjectFactory factory = instanceProjectFactoriesInBuildTool(
        rootPath).stream()
        .filter(ProjectFactory::isApplicable)
        .findFirst()
        .orElse(null);

    return factory != null ? (List<Path>) factory.getConfigPath() : Collections.emptyList();
  }
}
