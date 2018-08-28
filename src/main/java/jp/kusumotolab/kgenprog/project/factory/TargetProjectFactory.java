package jp.kusumotolab.kgenprog.project.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
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
      final List<Path> pathsForTestSource, List<ClassPath> classPaths, JUnitVersion junitVersion) {

    log.info("enter create(Path, List<Path>, List<Path>, List<ClassPath>, JUnitVersion)");

    final List<ProductSourcePath> productSourcePaths =
        getJavaFilePaths(pathsForProductSource).stream()
            .map(ProductSourcePath::new)
            .collect(Collectors.toList());
    final List<TestSourcePath> testSourcePaths = getJavaFilePaths(pathsForTestSource).stream()
        .map(TestSourcePath::new)
        .collect(Collectors.toList());

    return new DefaultProjectFactory(rootPath, productSourcePaths, testSourcePaths, classPaths,
        junitVersion).create();
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
   * ディレクトリを含みうるList<Path>から，JavaファイルのみのList<Path>を返す
   * 
   * @param paths
   * @return
   */
  private static List<Path> getJavaFilePaths(final List<Path> paths) {
    final List<Path> javaFilePaths = new ArrayList<>();
    for (final Path path : paths) {

      if (Files.isRegularFile(path) && endsWith(path, ".java")) {
        javaFilePaths.add(path);
        continue;
      }

      if (Files.isDirectory(path)) {
        try {
          final Set<Path> javaPaths = Files.walk(path)
              .filter(p -> endsWith(p, ".java"))
              .collect(Collectors.toSet());
          javaFilePaths.addAll(javaPaths);
        } catch (final IOException e) {
          log.error("failed to read a directory \"{}\"", path.toString());
        }
        continue;
      }
    }

    return javaFilePaths;
  }

  private static boolean endsWith(final Path path, final String suffix) {
    return path.toString()
        .endsWith(suffix);
  }
}
