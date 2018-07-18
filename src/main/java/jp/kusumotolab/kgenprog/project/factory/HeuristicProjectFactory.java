package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class HeuristicProjectFactory implements IProjectFactory {

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

    final List<SourcePath> sourcePaths = FileUtils.listFiles(rootPath.toFile(), javaExtension, true)
        .stream()
        .filter(file -> !file.getName()
            .endsWith("Test.java"))
        .map(file -> file.toPath())
        .map(TargetSourcePath::new)
        .collect(Collectors.toList());

    final List<SourcePath> testPaths = FileUtils.listFiles(rootPath.toFile(), javaExtension, true)
        .stream()
        .filter(file -> file.getName()
            .endsWith("Test.java"))
        .map(file -> file.toPath())
        .map(TestSourcePath::new)
        .collect(Collectors.toList());

    final List<ClassPath> classPath = JUnitLibraryResolver.libraries.get(JUnitVersion.JUNIT4);

    return new TargetProject(rootPath, sourcePaths, testPaths, classPath);
  }

}
