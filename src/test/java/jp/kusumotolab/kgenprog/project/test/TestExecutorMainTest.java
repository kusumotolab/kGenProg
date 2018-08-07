package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BarFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BarTest01Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BarTest02Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BarTest03Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BarTest04Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BarTest05Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BarTestFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest01Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest02Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest03Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest04Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTestFqn;
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
        "-s", FooFqn.toString(), //
        "-t", FooTestFqn.toString()});

    // serialize対象のファイルがあるはず
    assertThat(TestResults.getSerFilePath()).exists();

    final TestResults result = TestResults.deserialize();

    // 実行されたテストは4個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FooTest01Fqn, FooTest02Fqn, FooTest03Fqn, FooTest04Fqn);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FooTest01Fqn).failed).isFalse();
    assertThat(result.getTestResult(FooTest02Fqn).failed).isFalse();
    assertThat(result.getTestResult(FooTest03Fqn).failed).isTrue();
    assertThat(result.getTestResult(FooTest04Fqn).failed).isFalse();

    // よってテストの成功率はこうなる
    assertThat(result.getSuccessRate()).isEqualTo(1.0 * 3 / 4);

    final TestResult fooTest01result = result.getTestResult(FooTest01Fqn);
    final TestResult fooTest04result = result.getTestResult(FooTest04Fqn);

    // FooTest.test01 実行によるFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(FooFqn).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // FooTest.test04 実行によるFooのバレッジはこうなるはず
    assertThat(fooTest04result.getCoverages(FooFqn).statuses).containsExactly(EMPTY, COVERED, EMPTY,
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
        "-s", FooFqn.toString() + TestExecutorMain.SEPARATOR + BarFqn.toString(), //
        "-t", FooTestFqn.toString() + TestExecutorMain.SEPARATOR + BarTestFqn.toString()});

    // serialize対象のファイルがあるはず
    assertThat(TestResults.getSerFilePath()).exists();

    final TestResults result = TestResults.deserialize();

    // 実行されたテストは10個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FooTest01Fqn, FooTest02Fqn, FooTest03Fqn, FooTest04Fqn, //
        BarTest01Fqn, BarTest02Fqn, BarTest03Fqn, BarTest04Fqn, BarTest05Fqn);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FooTest01Fqn).failed).isFalse();
    assertThat(result.getTestResult(FooTest02Fqn).failed).isFalse();
    assertThat(result.getTestResult(FooTest03Fqn).failed).isTrue();
    assertThat(result.getTestResult(FooTest04Fqn).failed).isFalse();
    assertThat(result.getTestResult(BarTest01Fqn).failed).isFalse();
    assertThat(result.getTestResult(BarTest02Fqn).failed).isFalse();
    assertThat(result.getTestResult(BarTest03Fqn).failed).isFalse();
    assertThat(result.getTestResult(BarTest04Fqn).failed).isFalse();
    assertThat(result.getTestResult(BarTest05Fqn).failed).isFalse();

    final TestResult fooTest01result = result.getTestResult(FooTest01Fqn);

    // FooTest.test01()ではFooとBarが実行されたはず
    assertThat(fooTest01result.getExecutedTargetFQNs()).containsExactlyInAnyOrder(FooFqn, BarFqn);

    // FooTest.test01()で実行されたFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(FooFqn).statuses).containsExactlyInAnyOrder(EMPTY,
        COVERED, EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // BarTest.test01()ではFooとBarが実行されたはず
    final TestResult barTest01r = result.getTestResult(BarTest01Fqn);
    assertThat(barTest01r.getExecutedTargetFQNs()).containsExactlyInAnyOrder(FooFqn, BarFqn);

    // BarTest.test01()で実行されたBarのカバレッジはこうなるはず
    assertThat(barTest01r.getCoverages(BarFqn).statuses).containsExactlyInAnyOrder(EMPTY,
        NOT_COVERED, EMPTY, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, EMPTY, NOT_COVERED,
        NOT_COVERED);

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
        "-s", FooFqn.toString(), //
        "-t", FooTestFqn.toString()});
  }

  @Test(expected = CmdLineException.class)
  public void testMainFailureByInvalidArgs() throws Exception {

    // 例外を吐くはず
    TestExecutorMain.main(new String[] { //
        // "-b", workPath.toString(), // workPathを指定しない
        "-s", FooFqn.toString(), //
        "-t", FooTestFqn.toString()});
  }
}
