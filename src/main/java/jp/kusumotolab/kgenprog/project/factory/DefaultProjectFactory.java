package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class DefaultProjectFactory implements ProjectFactory {

  private final Path rootPath;
  private final List<ProductSourcePath> productSourcePaths;
  private final List<TestSourcePath> testSourcePaths;
  private final List<ClassPath> classPaths;

  public DefaultProjectFactory(final Path rootPath,
      final List<ProductSourcePath> productSourcePaths, final List<TestSourcePath> testSourcePaths,
      List<ClassPath> classPaths, JUnitVersion junitVersion) {
    this.rootPath = rootPath;
    this.productSourcePaths = productSourcePaths;
    this.testSourcePaths = testSourcePaths;

    // create new instance to avoid UnsupportedOperationException
    this.classPaths = new ArrayList<ClassPath>(classPaths);
    this.classPaths.addAll(JUnitLibraryResolver.libraries.get(junitVersion));
  }

  @Override
  public TargetProject create() {
    return new TargetProject(rootPath, productSourcePaths, testSourcePaths, classPaths);
  }

  @Override
  public boolean isApplicable() {
    return true;
  }

}
