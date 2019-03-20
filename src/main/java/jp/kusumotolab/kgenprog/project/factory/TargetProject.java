package jp.kusumotolab.kgenprog.project.factory;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TargetProject project = (TargetProject) o;
    return Objects.equals(rootPath, project.rootPath)
        && Objects.equals(getProductSourcePaths(), project.getProductSourcePaths())
        && Objects.equals(getTestSourcePaths(), project.getTestSourcePaths())
        && Objects.equals(getClassPaths(), project.getClassPaths());
  }

  @Override
  public int hashCode() {
    return Objects.hash(rootPath, getProductSourcePaths(), getTestSourcePaths(), getClassPaths());
  }

  @Override
  public String toString() {
    return rootPath.toString();
  }
}
