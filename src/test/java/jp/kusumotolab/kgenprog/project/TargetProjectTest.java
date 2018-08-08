package jp.kusumotolab.kgenprog.project;

import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.Bar;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.BarTest;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.Baz;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.BazTest;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.Foo;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.FooTest;
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

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath.resolve(FooTest));

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath);
  }

  @Test
  public void testGenerate02() throws IOException {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath.resolve(FooTest));
    final ProductSourcePath barPath = new ProductSourcePath(rootPath.resolve(Bar));
    final TestSourcePath barTestPath = new TestSourcePath(rootPath.resolve(BarTest));

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath, barPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath, barTestPath);
  }

  @Test
  public void testGenerate03() throws IOException {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath.resolve(FooTest));
    final ProductSourcePath barPath = new ProductSourcePath(rootPath.resolve(Bar));
    final TestSourcePath barTestPath = new TestSourcePath(rootPath.resolve(BarTest));
    final ProductSourcePath bazPath = new ProductSourcePath(rootPath.resolve(Baz));
    final TestSourcePath bazTestPath = new TestSourcePath(rootPath.resolve(BazTest));

    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath, barPath,
        bazPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath, barTestPath,
        bazTestPath);
  }
}
