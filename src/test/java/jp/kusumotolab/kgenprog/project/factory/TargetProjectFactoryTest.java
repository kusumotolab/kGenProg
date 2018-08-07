package jp.kusumotolab.kgenprog.project.factory;

import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.Bar;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.BarTest;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.Baz;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.BazTest;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.Foo;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Src.FooTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.test.ExampleAlias.Lib;

public class TargetProjectFactoryTest {

  private final static ClassPath junitClassPath = new ClassPath(Lib.Junit);
  private final static ClassPath hamcrestClassPath = new ClassPath(Lib.Hamcrest);

  @Test
  public void testCreateByBasePath01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath.resolve(FooTest));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(junitClassPath, hamcrestClassPath);
  }

  @Test
  public void testCreateByBasePath02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath.resolve(FooTest));
    final ProductSourcePath barPath = new ProductSourcePath(rootPath.resolve(Bar));
    final TestSourcePath barTestPath = new TestSourcePath(rootPath.resolve(BarTest));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath, barPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath, barTestPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(junitClassPath, hamcrestClassPath);
  }

  @Test
  public void testCreateByBasePath03() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath.resolve(FooTest));
    final ProductSourcePath barPath = new ProductSourcePath(rootPath.resolve(Bar));
    final TestSourcePath barTestPath = new TestSourcePath(rootPath.resolve(BarTest));
    final ProductSourcePath bazPath = new ProductSourcePath(rootPath.resolve(Baz));
    final TestSourcePath bazTestPath = new TestSourcePath(rootPath.resolve(BazTest));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath, barPath,
        bazPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath, barTestPath,
        bazTestPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(junitClassPath, hamcrestClassPath);
  }

  @Test
  public void testCreateByCompletelySpecified01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");

    final ProductSourcePath fooPath = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTestPath = new TestSourcePath(rootPath.resolve(FooTest));

    // 全パラメータを指定して生成
    final TargetProject project = TargetProjectFactory.create(rootPath, Arrays.asList(fooPath),
        Arrays.asList(fooTestPath), Collections.emptyList(), JUnitVersion.JUNIT4);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(fooPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTestPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(junitClassPath, hamcrestClassPath);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFailure01() {
    final Path rootPath = Paths.get("example/NonExistentProject");

    // Exception to be thrown
    TargetProjectFactory.create(rootPath);
  }

  @Test
  public void testFactorialBehavior01() throws IOException {
    // Factoryとして正しく振る舞っているかを確認．
    // スタブを実行する一時的なテスト．

    final Path rootPath = Paths.get("example/BuildSuccess01");

    // runtime exceptionを隠すためにsystem.errを退避して無効化
    final PrintStream ps = System.err;
    System.setErr(new PrintStream(new OutputStream() {

      @Override
      public void write(int b) {} // 何もしないwriter
    }));

    // 一時的にダミーbuild.xmlを生成
    final Path configPath = rootPath.resolve("build.xml");
    Files.createFile(configPath);

    // Factory.createしてみる
    final TargetProject project = TargetProjectFactory.create(rootPath);

    // ダミーのbuild.xmlが存在するので，AntProjectBuilderが起動し，nullなTargetProjectが返ってくるはず
    assertThat(project).isNull();

    // 後処理
    Files.delete(configPath);
    System.setErr(ps);

    // TODO
    // assertがfailするとファイルが削除されない．
    // 作用を残したままになってしまい，他のテストが巻き込まれて死ぬ．
    // https://github.com/kusumotolab/kGenProg/issues/97
  }

}
