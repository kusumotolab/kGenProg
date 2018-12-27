package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
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
    final String[] javaExtension = {"java"};

    final List<ProductSourcePath> productSourcePaths =
        FileUtils.listFiles(rootPath.toFile(), javaExtension, true)
            .stream()
            .filter(file -> !file.getName()
                .endsWith("Test.java"))
            .map(file -> file.toPath())
            .map(p -> ProductSourcePath.relativizeAndCreate(rootPath, p))
            .collect(Collectors.toList());

    final List<TestSourcePath> testSourcePaths =
        FileUtils.listFiles(rootPath.toFile(), javaExtension, true)
            .stream()
            .filter(file -> file.getName()
                .endsWith("Test.java"))
            .map(file -> file.toPath())
            .map(p -> TestSourcePath.relativizeAndCreate(rootPath, p))
            .collect(Collectors.toList());

    final List<ClassPath> classPath = JUnitLibraryResolver.libraries.get(JUnitVersion.JUNIT4);

    return new TargetProject(rootPath, productSourcePaths, testSourcePaths, classPath);
  }

}
