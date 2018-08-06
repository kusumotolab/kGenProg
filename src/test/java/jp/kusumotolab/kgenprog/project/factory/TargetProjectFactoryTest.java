package jp.kusumotolab.kgenprog.project.factory;

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

public class TargetProjectFactoryTest {

  // aliases for tested elements
  private final static String bc = "src/jp/kusumotolab/BuggyCalculator.java";
  private final static String bct = "src/jp/kusumotolab/BuggyCalculatorTest.java";
  private final static String ut = "src/jp/kusumotolab/Util.java";
  private final static String utt = "src/jp/kusumotolab/UtilTest.java";
  private final static String ju = "lib/junit4/junit-4.12.jar";
  private final static String hm = "lib/junit4/hamcrest-core-1.3.jar";

  private final static ClassPath juPath = new ClassPath(Paths.get(ju));
  private final static ClassPath hmPath = new ClassPath(Paths.get(hm));

  @Test
  public void testCreateByBasePath01() {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath bcPath = new ProductSourcePath(rootPath.resolve(bc));
    final TestSourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(bcPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(bctPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(juPath, hmPath);
  }

  @Test
  public void testCreateByBasePath02() {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    final ProductSourcePath bcPath = new ProductSourcePath(rootPath.resolve(bc));
    final TestSourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));
    final ProductSourcePath utPath = new ProductSourcePath(rootPath.resolve(ut));
    final TestSourcePath uttPath = new TestSourcePath(rootPath.resolve(utt));

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(bcPath, utPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(bctPath, uttPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(juPath, hmPath);
  }

  @Test
  public void testCreateByCompletelySpecified01() {
    final Path rootPath = Paths.get("example/CloseToZero01");

    final ProductSourcePath bcPath = new ProductSourcePath(rootPath.resolve(bc));
    final TestSourcePath bctPath = new TestSourcePath(rootPath.resolve(bct));

    // 全パラメータを指定して生成
    final TargetProject project = TargetProjectFactory.create(rootPath, Arrays.asList(bcPath),
        Arrays.asList(bctPath), Collections.emptyList(), JUnitVersion.JUNIT4);

    assertThat(project.rootPath).isSameAs(rootPath);
    assertThat(project.getProductSourcePaths()).containsExactlyInAnyOrder(bcPath);
    assertThat(project.getTestSourcePaths()).containsExactlyInAnyOrder(bctPath);
    assertThat(project.getClassPaths()).containsExactlyInAnyOrder(juPath, hmPath);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFailure01() {
    final Path rootPath = Paths.get("example/CloseToZero01xxxxxxxxx");

    // Exception to be thrown
    TargetProjectFactory.create(rootPath);
  }

  @Test
  public void testFactorialBehavior01() throws IOException {
    // Factoryとして正しく振る舞っているかを確認．
    // スタブを実行する一時的なテスト．

    final Path rootPath = Paths.get("example/CloseToZero01");

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
  }

}
