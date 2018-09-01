package jp.kusumotolab.kgenprog.project.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class DefaultTargetProjectFactory implements ProjectFactory {

  private static final Logger log = LoggerFactory.getLogger(DefaultTargetProjectFactory.class);

  private final Path rootPath;
  private final List<ProductSourcePath> productSourcePaths;
  private final List<TestSourcePath> testSourcePaths;
  private final List<ClassPath> classPaths;

  public DefaultTargetProjectFactory(final Path rootPath, final List<Path> pathsForProductSource,
      final List<Path> pathsForTestSource, List<Path> pathsForClass, JUnitVersion junitVersion) {

    log.info(
        "enter DefaultTargetProjectFatory(Path, List<Path>, List<Path>, List<Path>, JUnitVersion)");

    this.rootPath = rootPath;
    this.productSourcePaths = getFilePaths(rootPath, pathsForProductSource, ".java").stream()
        .map(ProductSourcePath::new)
        .collect(Collectors.toList());
    this.testSourcePaths = getFilePaths(rootPath, pathsForTestSource, ".java").stream()
        .map(TestSourcePath::new)
        .collect(Collectors.toList());
    this.classPaths = getFilePaths(rootPath, pathsForClass, ".class", ".jar").stream()
        .map(ClassPath::new)
        .collect(Collectors.toList());
    this.classPaths.addAll(JUnitLibraryResolver.libraries.get(junitVersion));

    log.info(
        "exit DefaultTargetProjectFatory(Path, List<Path>, List<Path>, List<Path>, JUnitVersion)");
  }

  @Override
  public TargetProject create() {
    log.info("enter create()");
    return new TargetProject(rootPath, productSourcePaths, testSourcePaths, classPaths);
  }

  @Override
  public boolean isApplicable() {
    log.info("enter isApplicable()");
    return true;
  }

  /**
   * ディレクトリを含みうるList<Path>から，JavaファイルのみのList<Path>を返す
   * 
   * @param paths
   * @return
   */
  private static List<Path> getFilePaths(final Path projectRootPath, final List<Path> paths,
      String... suffixes) {
    final List<Path> javaFilePaths = new ArrayList<>();
    for (final Path pathInProject : paths) {

      final Path path = projectRootPath.resolve(pathInProject);

      if (Files.isRegularFile(path) && endsWith(path, suffixes)) {
        javaFilePaths.add(path);
        continue;
      }

      if (Files.isDirectory(path)) {
        try {
          final Set<Path> javaPaths = Files.walk(path)
              .filter(p -> endsWith(p, suffixes))
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

  private static boolean endsWith(final Path path, final String... suffixes) {
    final String pathName = path.toString();
    return Stream.of(suffixes)
        .anyMatch(s -> pathName.endsWith(s));
  }
}
