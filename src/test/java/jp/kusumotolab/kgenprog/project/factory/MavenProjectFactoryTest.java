package jp.kusumotolab.kgenprog.project.factory;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class MavenProjectFactoryTest {

  @Test
  @Ignore
  public void create() {
    final Path path = Paths.get("example", "BuildSuccess05");
    final MavenProjectFactory mavenProjectFactory = new MavenProjectFactory(path);
    final TargetProject targetProject = mavenProjectFactory.create();

    final List<ProductSourcePath> productSourcePaths = targetProject.getProductSourcePaths();
    assertThat(productSourcePaths).hasSize(1);
    assertThat(productSourcePaths)
        .contains(new ProductSourcePath(path, Paths.get("src/main/java/example/Foo.java")));

    final List<TestSourcePath> testSourcePaths = targetProject.getTestSourcePaths();
    assertThat(testSourcePaths).hasSize(1);
    assertThat(testSourcePaths)
        .contains(new TestSourcePath(path, Paths.get("src/test/java/example/FooTest.java")));

    final List<ClassPath> classPaths = targetProject.getClassPaths();
    assertThat(classPaths).hasSize(2);
    assertThat(classPaths.get(0)
        .toString()).endsWith("junit-4.12.jar");
  }
}
