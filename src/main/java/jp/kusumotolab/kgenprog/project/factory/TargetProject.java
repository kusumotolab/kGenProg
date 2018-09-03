package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.List;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class TargetProject {

  public final Path rootPath; // TODO ひとまずrootPathだけpublicに．他フィールドは要検討
  private final List<ProductSourcePath> productSourcePaths;
  private List<TestSourcePath> testSourcePaths;
  private final List<ClassPath> classPaths;

  // Must be package-private. Should be created only from TargetProjectFactory#create
  TargetProject(final Path rootPath, final List<ProductSourcePath> productSourcePaths,
      final List<TestSourcePath> testSourcePaths, final List<ClassPath> classPaths) {
    this.rootPath = rootPath;
    this.productSourcePaths = productSourcePaths;
    this.testSourcePaths = testSourcePaths;
    this.classPaths = classPaths;
  }

  public List<ProductSourcePath> getProductSourcePaths() {
    return productSourcePaths;
  }

  public List<TestSourcePath> getTestSourcePaths() {
    return testSourcePaths;
  }

  public List<ClassPath> getClassPaths() {
    return classPaths;
  }

  public void setTestSourcePaths(final List<TestSourcePath> testSourcePaths) {
    this.testSourcePaths = testSourcePaths;
  }
}
