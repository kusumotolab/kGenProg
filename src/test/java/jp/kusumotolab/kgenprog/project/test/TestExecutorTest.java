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
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BazAnonymousFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BazInnerFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BazOuterFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.BazStaticInnerFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest01Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest02Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest03Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest04Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTestFqn;
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

  private TestResults generateTestResultsForExample01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);

    final TestExecutor executor = new TestExecutor();
    return executor.exec(new ClassPath(workPath), Arrays.asList(FooFqn), Arrays.asList(FooTestFqn));
  }

  private TestResults generateTestResultsForExample02() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);

    final TestExecutor executor = new TestExecutor();
    return executor.exec(new ClassPath(workPath), Arrays.asList(FooFqn, BarFqn),
        Arrays.asList(FooTestFqn, BarTestFqn));
  }

  @SuppressWarnings("unused")
  private TestResults generateTestResultsForExample03() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    new ProjectBuilder(targetProject).build(generatedSourceCode, workPath);

    final TestExecutor executor = new TestExecutor();
    return executor.exec(new ClassPath(workPath),
        Arrays.asList(FooFqn, BarFqn, BazInnerFqn, BazStaticInnerFqn, BazAnonymousFqn, BazOuterFqn),
        Arrays.asList(FooTestFqn, BarTestFqn));
  }

  @Test
  public void testTestExecutorForBuildSuccess01() throws Exception {
    final TestResults result = generateTestResultsForExample01();

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
  public void testTestExecutorForBuildSuccess02() throws Exception {
    final TestResults result = generateTestResultsForExample02();

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

  @Test
  public void testTestExecutorForBuildSuccess03() throws Exception {
    // TODO
    // Should confirm BuildSuccess03
  }

}
