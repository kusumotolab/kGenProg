package jp.kusumotolab.kgenprog.project.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class HeuristicProjectFactory implements ProjectFactory {

  final Path rootPath;

  public HeuristicProjectFactory(final Path rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public boolean isApplicable() {
    return true;
  }

  @Override
  public TargetProject create() {
    final List<ProductSourcePath> productSourcePaths = listFiles(rootPath, ".java") //
        .filter(p -> !p.toString()
            .endsWith("Test.java"))
        .map(p -> ProductSourcePath.relativizeAndCreate(rootPath, p))
        .collect(Collectors.toList());

    final List<TestSourcePath> testSourcePaths = listFiles(rootPath, ".java") //
        .filter(p -> p.toString()
            .endsWith("Test.java"))
        .map(p -> TestSourcePath.relativizeAndCreate(rootPath, p))
        .collect(Collectors.toList());

    final List<ClassPath> classPath = JUnitLibraryResolver.libraries.get(JUnitVersion.JUNIT4);

    return new TargetProject(rootPath, productSourcePaths, testSourcePaths, classPath);
  }

  private Stream<Path> listFiles(final Path path, final String extension) {
    try {
      return Files.walk(rootPath)
          .filter(Files::isRegularFile)
          .filter(p -> p.toString()
              .endsWith(extension));
    } catch (final IOException e) {
      return Stream.empty();
    }
  }

}
