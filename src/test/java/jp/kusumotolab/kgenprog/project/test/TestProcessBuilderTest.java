package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.Foo;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest01;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest02;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest03;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FooTest04;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class TestProcessBuilderTest {

  private final static Path WorkPath = Paths.get("tmp/work");

  @Before
  public void before() throws IOException {
    TestUtil.deleteWorkDirectory(WorkPath);
    Files.deleteIfExists(TestResults.getSerFilePath());
  }

  @Test
  public void testStart01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, WorkPath);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults result = builder.start(variant.getGeneratedSourceCode());

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
  public void testStartWithOtherworkPath01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");

    // exampleとは全く別のworkPathで動作確認
    final Path workPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-tmp");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workPath);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs()).hasSize(4);
  }

  @Test
  public void testStartWithOtherworkPath02() {
    // 絶対パスにしてみる
    final Path rootPath = Paths.get("example/BuildSuccess01")
        .toAbsolutePath();

    // exampleとは全く別のworkPathで動作確認
    final Path workPath = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-tmp");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workPath);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs()).hasSize(4);
  }

  @Test
  public void testBuildFailure01() throws IOException {
    final Path rootPath = Paths.get("example/BuildFailure01");

    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, WorkPath);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

    assertThat(r).isInstanceOf(EmptyTestResults.class);
    assertThat(r.getExecutedTestFQNs()).isEmpty();
    assertThat(r.getSuccessedTestResults()).isEmpty();
    assertThat(r.getFailedTestResults()).isEmpty();
    assertThat(r.getSuccessRate()).isNaN();
  }

}
