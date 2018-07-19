package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TestExecutorTest {

  final static String bc = "jp.kusumotolab.BuggyCalculator";
  final static String bct = "jp.kusumotolab.BuggyCalculatorTest";
  final static FullyQualifiedName buggyCalculator = new TargetFullyQualifiedName(bc);
  final static FullyQualifiedName buggyCalculatorTest = new TestFullyQualifiedName(bct);

  final static String ut = "jp.kusumotolab.Util";
  final static String utt = "jp.kusumotolab.UtilTest";
  final static FullyQualifiedName util = new TargetFullyQualifiedName(ut);
  final static FullyQualifiedName utilTest = new TestFullyQualifiedName(utt);

  final static FullyQualifiedName inner = new TargetFullyQualifiedName(bc + "$InnerClass");
  final static FullyQualifiedName stInner = new TargetFullyQualifiedName(bc + "$StaticInnerClass");
  final static FullyQualifiedName anonymous = new TargetFullyQualifiedName(bc + "$1");
  final static FullyQualifiedName outer = new TargetFullyQualifiedName("jp.kusumotolab.OuterClass");

  final static FullyQualifiedName test01 = new TestFullyQualifiedName(bct + ".test01");
  final static FullyQualifiedName test02 = new TestFullyQualifiedName(bct + ".test02");
  final static FullyQualifiedName test03 = new TestFullyQualifiedName(bct + ".test03");
  final static FullyQualifiedName test04 = new TestFullyQualifiedName(bct + ".test04");

  final static FullyQualifiedName plusTest01 = new TestFullyQualifiedName(utt + ".plusTest01");
  final static FullyQualifiedName plusTest02 = new TestFullyQualifiedName(utt + ".plusTest02");
  final static FullyQualifiedName minusTest01 = new TestFullyQualifiedName(utt + ".minusTest01");
  final static FullyQualifiedName minusTest02 = new TestFullyQualifiedName(utt + ".minusTest02");
  final static FullyQualifiedName dummyTest01 = new TestFullyQualifiedName(utt + ".dummyTest01");

  private TestResults generateTestResultsForExample01() throws Exception {
    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);

    final TestExecutor executor = new TestExecutor();
    return executor.exec(new ClassPath(outDir), Arrays.asList(buggyCalculator),
        Arrays.asList(buggyCalculatorTest));
  }

  private TestResults generateTestResultsForExample02() throws Exception {
    final Path rootDir = Paths.get("example/example02");
    final Path outDir = rootDir.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);

    final TestExecutor executor = new TestExecutor();
    return executor.exec(new ClassPath(outDir), Arrays.asList(buggyCalculator, util),
        Arrays.asList(buggyCalculatorTest, utilTest));
  }

  @SuppressWarnings("unused")
  private TestResults generateTestResultsForExample03() throws Exception {
    final Path rootDir = Paths.get("example/example03");
    final Path outDir = rootDir.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);

    final TestExecutor executor = new TestExecutor();
    return executor.exec(new ClassPath(outDir),
        Arrays.asList(buggyCalculator, util, inner, stInner, outer),
        Arrays.asList(buggyCalculatorTest, utilTest));
  }

  @Test
  public void testTestExecutorForExample01() throws Exception {
    final TestResults r = generateTestResultsForExample01();

    // example01で実行されたテストは4つのはず
    assertThat(r.getExecutedTestFQNs()).containsExactlyInAnyOrder(test01, test02, test03, test04);

    // テストの成否はこうなるはず
    assertThat(r.getTestResult(test01).failed).isFalse();
    assertThat(r.getTestResult(test02).failed).isFalse();
    assertThat(r.getTestResult(test03).failed).isTrue();
    assertThat(r.getTestResult(test04).failed).isFalse();

    final TestResult tr01 = r.getTestResult(test01);
    final TestResult tr04 = r.getTestResult(test04);

    // test01()ではBuggyCalculatorのみが実行されたはず
    assertThat(tr01.getExecutedTargetFQNs()).containsExactlyInAnyOrder(buggyCalculator);

    // test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
    assertThat(tr01.getCoverages(buggyCalculator).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // test04()で実行されたbuggyCalculatorのバレッジはこうなるはず
    assertThat(tr04.getCoverages(buggyCalculator).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, NOT_COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED);
  }

  @Test
  public void testTestExecutorForExample02() throws Exception {
    final TestResults r = generateTestResultsForExample02();

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

  // @Test
  // public void testTestExecutorForExample03() throws Exception {
  // final TestResults r = generateTestResultsForExample03();
  //
  // // example03で実行されたテストは10個のはず
  // assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder( //
  // test01, test02, test03, test04, //
  // plusTest01, plusTest02, minusTest01, minusTest02, dummyTest01)));
  //
  // // テストの成否はこうなるはず
  // assertThat(r.getTestResult(test01).failed, is(false));
  // assertThat(r.getTestResult(test02).failed, is(false));
  // assertThat(r.getTestResult(test03).failed, is(true));
  // assertThat(r.getTestResult(test04).failed, is(false));
  //
  // // test01()ではBuggyCalculator，Util，Inner，StaticInner, Outerが実行されたはず
  // final TestResult test01_result = r.getTestResult(test01);
  // assertThat(test01_result.getExecutedTargetFQNs(),
  // is(containsInAnyOrder(buggyCalculator, util, inner, staticInner, outer)));
  //
  // // 無名クラスは計測できないらしい（そもそもテストで実行された扱いにすらならない）．
  // assertThat(test01_result.getExecutedTargetFQNs(), not(hasItem(anonymousClass)));
  //
  // // test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
  // assertThat(test01_result.getCoverages(buggyCalculator).statuses, //
  // is(contains(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, COVERED, EMPTY, COVERED, COVERED,
  // EMPTY, NOT_COVERED, EMPTY, EMPTY, COVERED, COVERED, EMPTY, EMPTY, COVERED, EMPTY, EMPTY,
  // COVERED, COVERED, EMPTY, EMPTY, COVERED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, COVERED,
  // COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED)));
  //
  // // test01()で実行されたStaticInnerのカバレッジはこうなるはず
  // assertThat(test01_result.getCoverages(staticInner).statuses, //
  // is(contains(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
  // EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
  // EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
  // EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
  // EMPTY, NOT_COVERED, EMPTY, COVERED, COVERED)));
  //
  // // plusTest01()ではBuggyCalculator，Util，Inner，StaticInner, Outerが実行されたはず
  // final TestResult plusTest01_result = r.getTestResult(plusTest01);
  // assertThat(plusTest01_result.getExecutedTargetFQNs(),
  // is(containsInAnyOrder(buggyCalculator, inner, staticInner, outer, util)));
  //
  // // plusTest01()で実行されたUtilのカバレッジはこうなるはず
  // assertThat(plusTest01_result.getCoverages(util).statuses, //
  // is(contains(EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED, EMPTY, EMPTY, EMPTY, NOT_COVERED,
  // EMPTY, EMPTY, EMPTY, EMPTY, NOT_COVERED, NOT_COVERED)));
  // // TODO 最後のNOT_COVERDだけ理解できない．謎．
  // }

}
