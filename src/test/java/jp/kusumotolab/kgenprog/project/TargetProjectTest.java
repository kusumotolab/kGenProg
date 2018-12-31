package jp.kusumotolab.kgenprog.project;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAR_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAZ;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BAZ_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FOO_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TargetProjectTest {

  @Test
  public void testGenerate01() throws IOException {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath, FOO_TEST);

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath);
  }

  @Test
  public void testGenerate02() throws IOException {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath, FOO_TEST);
    final ProductSourcePath barPath = new ProductSourcePath(rootPath, BAR);
    final TestSourcePath barTestPath = new TestSourcePath(rootPath, BAR_TEST);

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath, barPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath, barTestPath);
  }

  @Test
  public void testGenerate03() throws IOException {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath, FOO);
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath, FOO_TEST);
    final ProductSourcePath barPath = new ProductSourcePath(rootPath, BAR);
    final TestSourcePath barTestPath = new TestSourcePath(rootPath, BAR_TEST);
    final ProductSourcePath bazPath = new ProductSourcePath(rootPath, BAZ);
    final TestSourcePath bazTestPath = new TestSourcePath(rootPath, BAZ_TEST);

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath, barPath,
        bazPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath, barTestPath,
        bazTestPath);
  }
}
