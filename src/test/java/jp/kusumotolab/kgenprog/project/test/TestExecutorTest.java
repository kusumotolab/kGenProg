package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TestExecutorTest {

  final static TargetFullyQualifiedName buggyCalculator =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator");
  final static TestFullyQualifiedName buggyCalculatorTest =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest");
  final static TargetFullyQualifiedName util = new TargetFullyQualifiedName("jp.kusumotolab.Util");
  final static TestFullyQualifiedName utilTest =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest");

  final static TargetFullyQualifiedName inner =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator$InnerClass");
  final static TargetFullyQualifiedName staticInner =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator$StaticInnerClass");
  final static TargetFullyQualifiedName outer =
      new TargetFullyQualifiedName("jp.kusumotolab.OuterClass");
  final static TargetFullyQualifiedName anonymousClass =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator$1");

  final static TestFullyQualifiedName test01 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
  final static TestFullyQualifiedName test02 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
  final static TestFullyQualifiedName test03 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
  final static TestFullyQualifiedName test04 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");

  final static TestFullyQualifiedName plusTest01 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.plusTest01");
  final static TestFullyQualifiedName plusTest02 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.plusTest02");
  final static TestFullyQualifiedName minusTest01 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.minusTest01");
  final static TestFullyQualifiedName minusTest02 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.minusTest02");
  final static TestFullyQualifiedName dummyTest01 =
      new TestFullyQualifiedName("jp.kusumotolab.UtilTest.dummyTest01");

  private TestResults generateTestResultsForExample01() throws Exception {
    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);
    final TestExecutor executor = new TestExecutor(new URL[] {outDir.toUri().toURL()});
    return executor.exec(Arrays.asList(buggyCalculator), Arrays.asList(buggyCalculatorTest));
  }

  private TestResults generateTestResultsForExample02() throws Exception {
    final Path rootDir = Paths.get("example/example02");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);
    final TestExecutor executor = new TestExecutor(new URL[] {outDir.toUri().toURL()});
    return executor.exec(Arrays.asList(buggyCalculator, util),
        Arrays.asList(buggyCalculatorTest, utilTest));
  }

  @SuppressWarnings("unused")
  private TestResults generateTestResultsForExample03() throws Exception {
    final Path rootDir = Paths.get("example/example03");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);
    final TestExecutor executor = new TestExecutor(new URL[] {outDir.toUri().toURL()});
    return executor.exec(Arrays.asList(buggyCalculator, util, inner, staticInner, outer),
        Arrays.asList(buggyCalculatorTest, utilTest));
  }

  @Test
  public void testTestExecutorForExample01() throws Exception {
    final TestResults r = generateTestResultsForExample01();

    // example01で実行されたテストは4つのはず
    assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04)));

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
  public void testTestExecutorForExample02() throws Exception {
    final TestResults r = generateTestResultsForExample02();

    // example02で実行されたテストは10個のはず
    assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04,
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
