package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;

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
    final String[] jarExtension = {"jar"};

    final List<SourceFile> sourceFiles =
        FileUtils.listFiles(rootPath.toFile(), javaExtension, true).stream()
            .map(file -> file.toPath()).map(TargetSourceFile::new).collect(Collectors.toList());

    final List<SourceFile> testFiles = FileUtils.listFiles(rootPath.toFile(), javaExtension, true)
        .stream().filter(file -> file.getName().endsWith("Test.java")).map(file -> file.toPath())
        .map(TargetSourceFile::new).collect(Collectors.toList());

    final List<ClassPath> classPath =
        FileUtils.listFiles(Paths.get("lib/junit4/").toFile(), jarExtension, false).stream()
            .map(file -> file.toPath()).map(ClassPath::new).collect(Collectors.toList());

    return new TargetProject(rootPath, sourceFiles, testFiles, classPath);
  }

}
