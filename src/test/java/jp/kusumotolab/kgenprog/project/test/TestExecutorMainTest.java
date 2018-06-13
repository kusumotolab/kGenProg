package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestExecutorMainTest {

  final static FullyQualifiedName buggyCalculator =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator");
  final static FullyQualifiedName buggyCalculatorTest =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest");
  final static FullyQualifiedName util = new TargetFullyQualifiedName("jp.kusumotolab.Util");
  final static FullyQualifiedName utilTest = new TestFullyQualifiedName("jp.kusumotolab.UtilTest");

  final static FullyQualifiedName test01 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
  final static FullyQualifiedName test02 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
  final static FullyQualifiedName test03 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
  final static FullyQualifiedName test04 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");

  final static FullyQualifiedName plusTest01 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.plusTest01");
  final static FullyQualifiedName plusTest02 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.plusTest02");
  final static FullyQualifiedName minusTest01 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.minusTest01");
  final static FullyQualifiedName minusTest02 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.minusTest02");
  final static FullyQualifiedName dummyTest01 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.dummyTest01");

  @Before
  public void before() throws IOException {
    TestResults.getSerFilePath().toFile().delete();
  }

  @Test
  public void testMainSuccess01() throws Exception {
    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProject.generate(rootDir);
    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);

    TestExecutorMain.main(new String[] { //
        "-b", outDir.toString(), //
        "-s", buggyCalculator.toString(), //
        "-t", buggyCalculatorTest.toString()});

    // serialize対象のファイルがあるはず
    assertThat(Files.exists(TestResults.getSerFilePath()), is(true));

    final TestResults r = TestResults.deserialize();

    // example01で実行されたテストは4つのはず
    assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04)));
    assertThat(r.getSuccessRate(), is(1.0 * 3 / 4));

    // テストの成否はこうなるはず
    assertThat(r.getTestResult(test01).failed, is(false));
    assertThat(r.getTestResult(test02).failed, is(false));
    assertThat(r.getTestResult(test03).failed, is(true));
    assertThat(r.getTestResult(test04).failed, is(false));

    final TestResult test01_result = r.getTestResult(test01);
    final TestResult test04_result = r.getTestResult(test04);

    // test01()ではBuggyCalculatorのみが実行されたはず
    assertThat(test01_result.getExecutedTargetFQNs(), is(containsInAnyOrder(buggyCalculator)));

    // test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
    assertThat(test01_result.getCoverages(buggyCalculator).statuses, is(contains(EMPTY, COVERED,
        EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED)));

    // test04()で実行されたbuggyCalculatorのバレッジはこうなるはず
    assertThat(test04_result.getCoverages(buggyCalculator).statuses, is(contains(EMPTY, COVERED,
        EMPTY, COVERED, NOT_COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED)));

  }

  @Test
  public void testMainSuccess02() throws Exception {
    final Path rootDir = Paths.get("example/example02");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProject.generate(rootDir);
    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);

    TestExecutorMain.main(new String[] { //
        "-b", outDir.toString(), //
        "-s", buggyCalculator.toString() + TestExecutorMain.SEPARATOR + util.toString(), //
        "-t", buggyCalculatorTest.toString() + TestExecutorMain.SEPARATOR + utilTest.toString()});

    // serialize対象のファイルがあるはず
    assertThat(Files.exists(TestResults.getSerFilePath()), is(true));

    final TestResults r = TestResults.deserialize();

    // example02で実行されたテストは10個のはず
    assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder( //
        test01, test02, test03, test04, //
        plusTest01, plusTest02, minusTest01, minusTest02, dummyTest01)));

    // テストの成否はこうなるはず
    assertThat(r.getTestResult(test01).failed, is(false));
    assertThat(r.getTestResult(test02).failed, is(false));
    assertThat(r.getTestResult(test03).failed, is(true));
    assertThat(r.getTestResult(test04).failed, is(false));

    final TestResult test01_result = r.getTestResult(test01);

    // test01()ではBuggyCalculatorとUtilが実行されたはず
    assertThat(test01_result.getExecutedTargetFQNs(),
        is(containsInAnyOrder(buggyCalculator, util)));

    // test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
    assertThat(test01_result.getCoverages(buggyCalculator).statuses, is(contains(EMPTY, COVERED,
        EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED)));

    // plusTest01()ではBuggyCalculatorとUtilが実行されたはず
    final TestResult plusTest01_result = r.getTestResult(plusTest01);
    assertThat(plusTest01_result.getExecutedTargetFQNs(),
        is(containsInAnyOrder(buggyCalculator, util)));

    // plusTest01()で実行されたUtilのカバレッジはこうなるはず
    assertThat(plusTest01_result.getCoverages(util).statuses, is(contains(EMPTY, NOT_COVERED, EMPTY,
        COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, EMPTY, NOT_COVERED, NOT_COVERED)));

    // TODO 最後のNOT_COVERDだけ理解できない．謎．
  }

  @Test(expected = Exception.class)
  public void testMainFailureByInvalidOutDir() throws Exception {

    // rootDirがバグってる
    final Path rootDir = Paths.get("example/example01xxxxxxxx");
    final Path outDir = rootDir.resolve("_bin");

    // 例外を吐くはず（具体的にどの例外を吐くかはひとまず確認せず）
    TestExecutorMain.main(new String[] { //
        "-b", outDir.toString(), //
        "-s", buggyCalculator.toString(), //
        "-t", buggyCalculatorTest.toString()});
  }

  @Test(expected = CmdLineException.class)
  public void testMainFailureByInvalidArgs() throws Exception {

    // 例外を吐くはず
    TestExecutorMain.main(new String[] { //
        // "-b", outDir, // outDirを指定しない
        "-s", buggyCalculator.toString(), //
        "-t", buggyCalculatorTest.toString()});
  }
}
