package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.Bar;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BarTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BarTest01;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BarTest02;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BarTest03;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BarTest04;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BarTest05;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazAnonymous;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazInner;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazOuter;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BazStaticInner;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.Foo;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest01;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest02;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest03;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest04;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.Qux;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.QuxTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class TestExecutorTest {

  private final static Path WorkPath = Paths.get("tmp/work");
  private final static ClassPath classPath = new ClassPath(WorkPath);
  private final static long timeoutSeconds = 60;

  @Before
  public void before() throws IOException {
    TestUtil.deleteWorkDirectory(WorkPath);
  }

  @After
  public void after() throws IOException {
    TestUtil.deleteWorkDirectory(WorkPath);
  }

  @Test
  public void testTestExecutorForBuildSuccess01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode, WorkPath);

    final TestExecutor executor = new TestExecutor(timeoutSeconds);
    final TestResults result =
        executor.exec(Arrays.asList(classPath), Arrays.asList(Foo), Arrays.asList(FooTest));

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
  public void testTestExecutorForBuildSuccess02() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode, WorkPath);

    final TestExecutor executor = new TestExecutor(timeoutSeconds);
    final TestResults result = executor.exec(Arrays.asList(classPath), Arrays.asList(Foo, Bar),
        Arrays.asList(FooTest, BarTest));

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

  @Test
  public void testTestExecutorForBuildSuccess03() throws Exception {

    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode, WorkPath);

    final TestExecutor executor = new TestExecutor(timeoutSeconds);
    final TestResults result = executor.exec(Arrays.asList(classPath),
        Arrays.asList(Foo, Bar, BazInner, BazStaticInner, BazAnonymous, BazOuter),
        Arrays.asList(FooTest, BarTest));


    // TODO
    // Should confirm BuildSuccess03
    result.toString(); // to avoid unused warnings
  }

  @Test
  public void testTestExecutorForBuildSuccess04() throws Exception {

    final Path rootPath = Paths.get("example/BuildSuccess04");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final Variant variant = targetProject.getInitialVariant();
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode, WorkPath);

    final long timeout = 1;
    final TestExecutor executor = new TestExecutor(timeout);
    final TestResults result =
        executor.exec(Arrays.asList(classPath), Arrays.asList(Qux), Arrays.asList(QuxTest));

    // 無限ループが発生し，タイムアウトで打ち切られてEmptyになるはず
    assertThat(result).isInstanceOf(EmptyTestResults.class);
  }
}
