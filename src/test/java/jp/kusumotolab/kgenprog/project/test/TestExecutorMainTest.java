package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.Bar;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.BarTest;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.BarTest01;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.BarTest02;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.BarTest03;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.BarTest04;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.BarTest05;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.Foo;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.FooTest;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.FooTest01;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.FooTest02;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.FooTest03;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.Fqn.FooTest04;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TestExecutorMainTest {

  @Before
  public void before() throws IOException {
    Files.deleteIfExists(TestResults.getSerFilePath());
  }

  @Test
  public void testMainForBuildSuccess01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);

    TestExecutorMain.main(new String[] { //
        "-b", workPath.toString(), //
        "-s", Foo.toString(), //
        "-t", FooTest.toString()});

    // serialize対象のファイルがあるはず
    assertThat(TestResults.getSerFilePath()).exists();

    final TestResults result = TestResults.deserialize();

    // 実行されたテストは4個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FooTest01, FooTest02, FooTest03, FooTest04);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FooTest01).failed).isFalse();
    assertThat(result.getTestResult(FooTest02).failed).isFalse();
    assertThat(result.getTestResult(FooTest03).failed).isTrue();
    assertThat(result.getTestResult(FooTest04).failed).isFalse();

    // よってテストの成功率はこうなる
    assertThat(result.getSuccessRate()).isEqualTo(1.0 * 3 / 4);

    final TestResult fooTest01result = result.getTestResult(FooTest01);
    final TestResult fooTest04result = result.getTestResult(FooTest04);

    // FooTest.test01 実行によるFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(Foo).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // FooTest.test04 実行によるFooのバレッジはこうなるはず
    assertThat(fooTest04result.getCoverages(Foo).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, NOT_COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED);
  }

  @Test
  public void testMainForBuildSuccess02() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);

    TestExecutorMain.main(new String[] { //
        "-b", workPath.toString(), //
        "-s", Foo.toString() + TestExecutorMain.SEPARATOR + Bar.toString(), //
        "-t", FooTest.toString() + TestExecutorMain.SEPARATOR + BarTest.toString()});

    // serialize対象のファイルがあるはず
    assertThat(TestResults.getSerFilePath()).exists();

    final TestResults result = TestResults.deserialize();

    // 実行されたテストは10個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FooTest01, FooTest02, FooTest03, FooTest04, //
        BarTest01, BarTest02, BarTest03, BarTest04, BarTest05);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FooTest01).failed).isFalse();
    assertThat(result.getTestResult(FooTest02).failed).isFalse();
    assertThat(result.getTestResult(FooTest03).failed).isTrue();
    assertThat(result.getTestResult(FooTest04).failed).isFalse();
    assertThat(result.getTestResult(BarTest01).failed).isFalse();
    assertThat(result.getTestResult(BarTest02).failed).isFalse();
    assertThat(result.getTestResult(BarTest03).failed).isFalse();
    assertThat(result.getTestResult(BarTest04).failed).isFalse();
    assertThat(result.getTestResult(BarTest05).failed).isFalse();

    final TestResult fooTest01result = result.getTestResult(FooTest01);

    // FooTest.test01()ではFooとBarが実行されたはず
    assertThat(fooTest01result.getExecutedTargetFQNs()).containsExactlyInAnyOrder(Foo, Bar);

    // FooTest.test01()で実行されたFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(Foo).statuses).containsExactlyInAnyOrder(EMPTY, COVERED,
        EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // BarTest.test01()ではFooとBarが実行されたはず
    final TestResult barTest01r = result.getTestResult(BarTest01);
    assertThat(barTest01r.getExecutedTargetFQNs()).containsExactlyInAnyOrder(Foo, Bar);

    // BarTest.test01()で実行されたBarのカバレッジはこうなるはず
    assertThat(barTest01r.getCoverages(Bar).statuses).containsExactlyInAnyOrder(EMPTY, NOT_COVERED,
        EMPTY, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, EMPTY, NOT_COVERED, NOT_COVERED);

    // TODO 最後のNOT_COVERDだけ理解できない．謎．
  }

  @Test(expected = Exception.class)
  public void testMainFailureByInvalidrootPath() throws Exception {
    // rootPathがバグってる
    final Path rootPath = Paths.get("example/NonExistenceProject");
    final Path workPath = rootPath.resolve("bin");

    // 例外を吐くはず（具体的にどの例外を吐くかはひとまず確認せず）
    TestExecutorMain.main(new String[] { //
        "-b", workPath.toString(), //
        "-s", Foo.toString(), //
        "-t", FooTest.toString()});
  }

  @Test(expected = CmdLineException.class)
  public void testMainFailureByInvalidArgs() throws Exception {

    // 例外を吐くはず
    TestExecutorMain.main(new String[] { //
        // "-b", workPath.toString(), // workPathを指定しない
        "-s", Foo.toString(), //
        "-t", FooTest.toString()});
  }
}
