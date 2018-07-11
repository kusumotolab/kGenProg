package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class DefaultProjectFactory implements IProjectFactory {
  private final Path rootPath;
  private final List<SourceFile> sourceFiles;
  private final List<SourceFile> testFiles;
  private final List<ClassPath> classPaths;

  public DefaultProjectFactory(final Path rootPath, final List<SourceFile> sourceFiles,
      final List<SourceFile> testFiles, List<ClassPath> classPaths, JUnitVersion junitVersion) {
    this.rootPath = rootPath;
    this.sourceFiles = sourceFiles;
    this.testFiles = testFiles;

    // create new instance to avoid UnsupportedOperationException
    this.classPaths = new ArrayList<ClassPath>(classPaths);
    this.classPaths.addAll(JUnitLibraryResolver.libraries.get(junitVersion));
  }

  @Override
  public TargetProject create() {
    return new TargetProject(rootPath, sourceFiles, testFiles, classPaths);
  }

  @Override
  public boolean isApplicable() {
    return true;
  }

}
