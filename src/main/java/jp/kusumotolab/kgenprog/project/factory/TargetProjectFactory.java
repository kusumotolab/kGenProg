package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class TargetProjectFactory {

  private static final Logger log = LoggerFactory.getLogger(TargetProjectFactory.class);

  /**
   * TargetProjectを生成するファクトリメソッド． 各種ビルドツールの設定ファイルが存在すればそこから，
   * 存在しなければヒューリスティックにフォルダを探索してTargetProjectを生成する．
   * 
   * @param rootPath 対象のルートパス
   * @return TargetProject
   */
  public static TargetProject create(final Path rootPath) {
    ProjectFactory applicableFactory = instanceProjectFactories(rootPath).stream()
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
   * @param classPaths
   * @return TargetProject
   */
  public static TargetProject create(final Path rootPath, final List<Path> pathsForProductSource,
      final List<Path> pathsForTestSource, List<Path> pathsForClass, JUnitVersion junitVersion) {
    log.debug("enter create(Path, List<Path>, List<Path>, List<Path>, JUnitVersion)");
    return new DefaultTargetProjectFactory(rootPath, pathsForProductSource, pathsForTestSource,
        pathsForClass, junitVersion).create();
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
}
