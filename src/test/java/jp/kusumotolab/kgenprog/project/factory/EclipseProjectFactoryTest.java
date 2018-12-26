package jp.kusumotolab.kgenprog.project.factory;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class EclipseProjectFactoryTest {

  private final static ClassPath JUNIT_CLASSPATH =
      new ClassPath(Paths.get("example/BuildSuccess06/./lib/junit-4.12.jar"));

  @Test
  public void testCreateBySingleEclipseProject() {
    final Path rootPath = Paths.get("example/BuildSuccess06");
    final EclipseProjectFactory eclipseProjectFactory = new EclipseProjectFactory(rootPath);
    final TargetProject project = eclipseProjectFactory.create();

    final ProductSourcePath foo = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTest = new TestSourcePath(rootPath, FOO_TEST);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(JUNIT_CLASSPATH);
  }

}
