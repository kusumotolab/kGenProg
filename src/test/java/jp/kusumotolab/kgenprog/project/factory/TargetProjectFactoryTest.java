package jp.kusumotolab.kgenprog.project.factory;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.Bar;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BarTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.Baz;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.BazTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.Foo;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src.FooTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Lib;

public class TargetProjectFactoryTest {

  private final static ClassPath Junit = new ClassPath(Lib.Junit);
  private final static ClassPath Hamcrest = new ClassPath(Lib.Hamcrest);

  @Test
  public void testCreateByBasePath01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath foo = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTest = new TestSourcePath(rootPath.resolve(FooTest));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(Junit, Hamcrest);
  }

  @Test
  public void testCreateByBasePath02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath foo = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTest = new TestSourcePath(rootPath.resolve(FooTest));
    final ProductSourcePath bar = new ProductSourcePath(rootPath.resolve(Bar));
    final TestSourcePath barTest = new TestSourcePath(rootPath.resolve(BarTest));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo, bar);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest, barTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(Junit, Hamcrest);
  }

  @Test
  public void testCreateByBasePath03() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath foo = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTest = new TestSourcePath(rootPath.resolve(FooTest));
    final ProductSourcePath bar = new ProductSourcePath(rootPath.resolve(Bar));
    final TestSourcePath barTest = new TestSourcePath(rootPath.resolve(BarTest));
    final ProductSourcePath baz = new ProductSourcePath(rootPath.resolve(Baz));
    final TestSourcePath bazTest = new TestSourcePath(rootPath.resolve(BazTest));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo, bar, baz);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest, barTest, bazTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(Junit, Hamcrest);
  }

  @Test
  public void testCreateByCompletelySpecified01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");

    final List<Path> fooPath = Arrays.asList(Foo);
    final List<Path> fooTestPath = Arrays.asList(FooTest);

    // 全パラメータを指定して生成
    final TargetProject project = TargetProjectFactory.create(rootPath, fooPath, fooTestPath,
        Collections.emptyList(), JUnitVersion.JUNIT4);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths())
        .containsExactlyInAnyOrder(new ProductSourcePath(rootPath.resolve(Foo)));
    assertThat(project.getTestSourcePaths())
        .containsExactlyInAnyOrder(new TestSourcePath(rootPath.resolve(FooTest)));
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(Junit, Hamcrest);
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
    try {
      Files.createFile(configPath);
    } catch (final IOException e) {
      if (!Files.exists(configPath)) {
        // 一時ファイル生成失敗．でどうしようもないのでthrow
        throw e;
      }
    }

    // Factory.createしてみる
    final TargetProject project = TargetProjectFactory.create(rootPath);

    // 後処理（作用回避のため，assertの前に戻しておく）
    Files.deleteIfExists(configPath);
    System.setErr(ps);

    // ダミーのbuild.xmlが存在するので，AntProjectBuilderが起動し，nullなTargetProjectが返ってくるはず
    assertThat(project).isNull();

  }

  @Test
  public void testCreateBySpecifingPathsForProductAndTest() {
    final Path rootPath = Paths.get("example/BuildSuccess07");
    final List<Path> srcPaths = Arrays.asList(Paths.get("src"));
    final List<Path> testPaths = Arrays.asList(Paths.get("test"));
    final TargetProject project = TargetProjectFactory.create(rootPath, srcPaths, testPaths,
        Collections.emptyList(), JUnitVersion.JUNIT4);

    // *Testのパス名の中の "src" を "test" に書き換え
    final String fooTestStr = FooTest.toString()
        .replace("src", "test");
    final String barTestStr = BarTest.toString()
        .replace("src", "test");

    final ProductSourcePath foo = new ProductSourcePath(rootPath.resolve(Foo));
    final TestSourcePath fooTest = new TestSourcePath(rootPath.resolve(fooTestStr));
    final ProductSourcePath bar = new ProductSourcePath(rootPath.resolve(Bar));
    final TestSourcePath barTest = new TestSourcePath(rootPath.resolve(barTestStr));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(foo, bar);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(fooTest, barTest);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(Junit, Hamcrest);
  }
}
