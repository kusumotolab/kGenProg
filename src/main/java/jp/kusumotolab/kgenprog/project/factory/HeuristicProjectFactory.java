package jp.kusumotolab.kgenprog.project.factory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import jp.kusumotolab.kgenprog.project.TestSourceFile;

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

    final List<SourceFile> sourceFiles = new ArrayList<>();
    final List<SourceFile> testFiles = new ArrayList<>();

    final String[] extension = {"java"};
    final Collection<File> files = FileUtils.listFiles(rootPath.toFile(), extension, true);
    for (File file : files) {
      if (file.getName().endsWith("Test.java")) {
        testFiles.add(new TestSourceFile(file.toPath()));
      }
      // TODO テストファイルはsourceFilesにaddすべきではないのでは？
      sourceFiles.add(new TargetSourceFile(file.toPath()));
    }

    // TODO 固定lib名の修正
    final List<ClassPath> classPath = Arrays.asList( //
        new ClassPath(Paths.get("lib/junit4/junit-4.12.jar")), //
        new ClassPath(Paths.get("lib/junit4/hamcrest-core-1.3.jar")));

    return new TargetProject(rootPath, sourceFiles, testFiles, classPath);
  }

}
