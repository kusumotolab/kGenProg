package jp.kusumotolab.kgenprog.project.factory;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class MavenProjectFactoryTest {

  @Test
  public void testCreate() {
    final Path path = Paths.get("example", "BuildToolMaven");
    final MavenProjectFactory mavenProjectFactory = new MavenProjectFactory(path);
    final TargetProject targetProject = mavenProjectFactory.create();

    final List<ProductSourcePath> productSourcePaths = targetProject.getProductSourcePaths();
    assertThat(productSourcePaths)
        .containsOnly(new ProductSourcePath(path, Paths.get("src/main/java/example/Foo.java")));

    final List<TestSourcePath> testSourcePaths = targetProject.getTestSourcePaths();
    assertThat(testSourcePaths)
        .containsOnly(new TestSourcePath(path, Paths.get("src/test/java/example/FooTest.java")));

    final List<ClassPath> classPaths = targetProject.getClassPaths();
    assertThat(classPaths)
        .hasSize(2)
        .extracting(ClassPath::toString)
        .anySatisfy(s -> assertThat(s).endsWith("junit-4.13.1.jar"))
        .anySatisfy(s -> assertThat(s).endsWith("hamcrest-core-1.3.jar"));
  }
}
