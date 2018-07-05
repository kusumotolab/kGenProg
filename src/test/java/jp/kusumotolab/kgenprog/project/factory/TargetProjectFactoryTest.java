package jp.kusumotolab.kgenprog.project.factory;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;

public class TargetProjectFactoryTest {

  @Test
  public void testCreateByBasePath01() {
    final Path rootPath = Paths.get("./example/example01");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    assertThat(project.rootPath, is(rootPath));
    assertThat(project.getSourceFiles(), is(containsInAnyOrder( //
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")))));
    assertThat(project.getTestFiles(), is(containsInAnyOrder( //
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")))));
    assertThat(project.getClassPaths(), is(containsInAnyOrder( //
        new ClassPath(Paths.get("lib/junit4/junit-4.12.jar")),
        new ClassPath(Paths.get("lib/junit4/hamcrest-core-1.3.jar")))));
  }

  @Test
  public void testCreateByBasePath02() {
    final Path rootPath = Paths.get("./example/example02");
    final TargetProject project = TargetProjectFactory.create(rootPath);

    assertThat(project.rootPath, is(rootPath));
    assertThat(project.getSourceFiles(), is(containsInAnyOrder( //
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")),
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/Util.java")),
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/UtilTest.java")))));
    assertThat(project.getTestFiles(), is(containsInAnyOrder( //
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")),
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/UtilTest.java")))));
    assertThat(project.getClassPaths(), is(containsInAnyOrder( //
        new ClassPath(Paths.get("lib/junit4/junit-4.12.jar")),
        new ClassPath(Paths.get("lib/junit4/hamcrest-core-1.3.jar")))));
  }

  @Test
  public void testCreateByCompletelySpecified01() {
    final Path rootPath = Paths.get("./example/example01");
    final TargetProject project = TargetProjectFactory.create( //
        rootPath, //
        Arrays.asList( //
            new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
            new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java"))),
        Arrays.asList( //
            new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java"))),
        Arrays.asList(), //
        JUnitVersion.JUNIT4);

    assertThat(project.rootPath, is(rootPath));
    assertThat(project.getSourceFiles(), is(containsInAnyOrder( //
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculator.java")),
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")))));
    assertThat(project.getTestFiles(), is(containsInAnyOrder( //
        new TargetSourceFile(rootPath.resolve("src/jp/kusumotolab/BuggyCalculatorTest.java")))));
    assertThat(project.getClassPaths(), is(containsInAnyOrder( //
        new ClassPath(Paths.get("lib/junit4/junit-4.12.jar")),
        new ClassPath(Paths.get("lib/junit4/hamcrest-core-1.3.jar")))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFailure01() {
    final Path rootPath = Paths.get("./example/example01xxxxxxxxx");
    TargetProjectFactory.create(rootPath);
  }

  @Test
  public void testFactorialBehavior01() throws IOException {
    // Factoryとして正しく振る舞っているかを確認．
    // スタブを実行する一時的なテスト．

    final Path rootPath = Paths.get("./example/example01");

    // runtime exceptionを隠すためにsystem.errを退避して無効化
    final PrintStream ps = System.err;
    System.setErr(new PrintStream(new OutputStream() {
      @Override
      public void write(int b) {} // 何もしないwriter
    }));

    // 一時的にダミーbuild.xmlを生成
    final Path configFile = rootPath.resolve("build.xml");
    Files.createFile(configFile);

    // Factory.createしてみる
    final TargetProject project = TargetProjectFactory.create(rootPath);

    // ダミーのbuild.xmlが存在するので，AntProjectBuilderが起動し，nullなTargetProjectが返ってくるはず
    assertThat(project, is(nullValue()));

    // 後処理
    Files.delete(configFile);
    System.setErr(ps);
  }
}
