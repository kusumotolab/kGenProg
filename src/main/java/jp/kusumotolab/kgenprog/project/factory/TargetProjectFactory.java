package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourcePath;
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
    IProjectFactory applicableFactory = instanceProjectFactories(rootPath).stream()
        .filter(IProjectFactory::isApplicable)
        .findFirst()
        .orElse(new HeuristicProjectFactory(rootPath));
    return applicableFactory.create();
  }

  /**
   * @see TargetProjectFactory#create
   * @param rootPath
   * @return
   */
  public static TargetProject create(final String rootPath) {
    return create(Paths.get(rootPath));
  }

  /**
   * TargetProjectを生成するファクトリメソッド． 全パラメータを指定する必要あり．
   * 
   * @param rootPath 対象のルートパス
   * @param sourcePaths
   * @param testPaths
   * @param classPaths
   * @return TargetProject
   */
  public static TargetProject create(final Path rootPath, final List<SourcePath> sourcePaths,
      final List<SourcePath> testPaths, List<ClassPath> classPaths, JUnitVersion junitVersion) {
    return new DefaultProjectFactory(rootPath, sourcePaths, testPaths, classPaths, junitVersion)
        .create();
  }

  /**
   * ファクトリ一覧の生成
   * 
   * @param rootPath
   * @return
   */
  private static List<IProjectFactory> instanceProjectFactories(final Path rootPath) {
    return Arrays.asList(new AntProjectFactory(rootPath), new MavenProjectFactory(rootPath),
        new GradleProjectFactory(rootPath));
  }
}
