package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class DefaultProjectFactory implements IProjectFactory {

  private final Path rootPath;
  private final List<SourcePath> sourcePaths;
  private final List<SourcePath> testPaths;
  private final List<ClassPath> classPaths;

  public DefaultProjectFactory(final Path rootPath, final List<SourcePath> sourcePaths,
      final List<SourcePath> testPaths, List<ClassPath> classPaths, JUnitVersion junitVersion) {
    this.rootPath = rootPath;
    this.sourcePaths = sourcePaths;
    this.testPaths = testPaths;

    // create new instance to avoid UnsupportedOperationException
    this.classPaths = new ArrayList<ClassPath>(classPaths);
    this.classPaths.addAll(JUnitLibraryResolver.libraries.get(junitVersion));
  }

  @Override
  public TargetProject create() {
    return new TargetProject(rootPath, sourcePaths, testPaths, classPaths);
  }

  @Override
  public boolean isApplicable() {
    return true;
  }

}
