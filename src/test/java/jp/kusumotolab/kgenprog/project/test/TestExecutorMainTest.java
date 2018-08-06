package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
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

  final static String bc = "jp.kusumotolab.BuggyCalculator";
  final static String bct = "jp.kusumotolab.BuggyCalculatorTest";
  final static FullyQualifiedName buggyCalculator = new TargetFullyQualifiedName(bc);
  final static FullyQualifiedName buggyCalculatorTest = new TestFullyQualifiedName(bct);

  final static String ut = "jp.kusumotolab.Util";
  final static String utt = "jp.kusumotolab.UtilTest";
  final static FullyQualifiedName util = new TargetFullyQualifiedName(ut);
  final static FullyQualifiedName utilTest = new TestFullyQualifiedName(utt);

  final static FullyQualifiedName test01 = new TestFullyQualifiedName(bct + ".test01");
  final static FullyQualifiedName test02 = new TestFullyQualifiedName(bct + ".test02");
  final static FullyQualifiedName test03 = new TestFullyQualifiedName(bct + ".test03");
  final static FullyQualifiedName test04 = new TestFullyQualifiedName(bct + ".test04");

  final static FullyQualifiedName plusTest01 = new TestFullyQualifiedName(utt + ".plusTest01");
  final static FullyQualifiedName plusTest02 = new TestFullyQualifiedName(utt + ".plusTest02");
  final static FullyQualifiedName minusTest01 = new TestFullyQualifiedName(utt + ".minusTest01");
  final static FullyQualifiedName minusTest02 = new TestFullyQualifiedName(utt + ".minusTest02");
  final static FullyQualifiedName dummyTest01 = new TestFullyQualifiedName(utt + ".dummyTest01");

  @Before
  public void before() throws IOException {
    Files.deleteIfExists(TestResults.getSerFilePath());
  }

  @Test
  public void testMainSuccess01() throws Exception {
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);

    TestExecutorMain.main(new String[] { //
        "-b", workPath.toString(), //
        "-s", buggyCalculator.toString(), //
        "-t", buggyCalculatorTest.toString() });

    // serialize対象のファイルがあるはず
    assertThat(TestResults.getSerFilePath()).exists();

    final TestResults r = TestResults.deserialize();

    // テストの結果はこうなるはず
    assertThat(r.getExecutedTestFQNs()).hasSize(4);
    assertThat(r.getSuccessRate()).isEqualTo(1.0 * 3 / 4);
    assertThat(r.getExecutedTestFQNs()).containsExactlyInAnyOrder(test01, test02, test03, test04);
    assertThat(r.getTestResult(test01).failed).isFalse();
    assertThat(r.getTestResult(test02).failed).isFalse();
    assertThat(r.getTestResult(test03).failed).isTrue();
    assertThat(r.getTestResult(test04).failed).isFalse();

    final TestResult tr01 = r.getTestResult(test01);
    final TestResult tr04 = r.getTestResult(test04);

    // BuggyCalculatorTest.test01 実行によるbuggyCalculatorのカバレッジはこうなるはず
    assertThat(tr01.getCoverages(buggyCalculator).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // BuggyCalculatorTest.test04 実行によるbuggyCalculatorのバレッジはこうなるはず
    assertThat(tr04.getCoverages(buggyCalculator).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, NOT_COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED);
  }

  @Test
  public void testMainSuccess02() throws Exception {
    final Path rootPath = Paths.get("example/CloseToZero02");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);

    TestExecutorMain.main(new String[] { //
        "-b", workPath.toString(), //
        "-s", buggyCalculator.toString() + TestExecutorMain.SEPARATOR + util.toString(), //
        "-t", buggyCalculatorTest.toString() + TestExecutorMain.SEPARATOR + utilTest.toString() });

    // serialize対象のファイルがあるはず
    assertThat(TestResults.getSerFilePath()).exists();

    final TestResults r = TestResults.deserialize();

    // example02で実行されたテストは10個のはず
    assertThat(r.getExecutedTestFQNs()).containsExactlyInAnyOrder(test01, test02, test03, test04,
        plusTest01, plusTest02, minusTest01, minusTest02, dummyTest01);

    // テストの成否はこうなるはず
    assertThat(r.getTestResult(test01).failed).isFalse();
    assertThat(r.getTestResult(test02).failed).isFalse();
    assertThat(r.getTestResult(test03).failed).isTrue();
    assertThat(r.getTestResult(test04).failed).isFalse();

    final TestResult tr01 = r.getTestResult(test01);

    // test01()ではBuggyCalculatorとUtilが実行されたはず
    assertThat(tr01.getExecutedTargetFQNs()).containsExactlyInAnyOrder(buggyCalculator, util);

    // test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
    assertThat(tr01.getCoverages(buggyCalculator).statuses).containsExactlyInAnyOrder(EMPTY,
        COVERED, EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // plusTest01()ではBuggyCalculatorとUtilが実行されたはず
    final TestResult plusTest01result = r.getTestResult(plusTest01);
    assertThat(plusTest01result.getExecutedTargetFQNs()).containsExactlyInAnyOrder(buggyCalculator,
        util);

    // plusTest01()で実行されたUtilのカバレッジはこうなるはず
    assertThat(plusTest01result.getCoverages(util).statuses).containsExactlyInAnyOrder(EMPTY,
        NOT_COVERED, EMPTY, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, EMPTY, NOT_COVERED,
        NOT_COVERED);

    // TODO 最後のNOT_COVERDだけ理解できない．謎．
  }

  @Test(expected = Exception.class)
  public void testMainFailureByInvalidrootPath() throws Exception {

    // rootPathがバグってる
    final Path rootPath = Paths.get("example/CloseToZero01xxxxxxxx");
    final Path workPath = rootPath.resolve("bin");

    // 例外を吐くはず（具体的にどの例外を吐くかはひとまず確認せず）
    TestExecutorMain.main(new String[] { //
        "-b", workPath.toString(), //
        "-s", buggyCalculator.toString(), //
        "-t", buggyCalculatorTest.toString() });
  }

  @Test(expected = CmdLineException.class)
  public void testMainFailureByInvalidArgs() throws Exception {

    // 例外を吐くはず
    TestExecutorMain.main(new String[] { //
        // "-b", workPath.toString(), // workPathを指定しない
        "-s", buggyCalculator.toString(), //
        "-t", buggyCalculatorTest.toString() });
  }
}
