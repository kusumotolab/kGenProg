package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooFqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest01Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest02Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest03Fqn;
import static jp.kusumotolab.kgenprog.project.test.ExampleAlias.FooTest04Fqn;
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

public class TestProcessBuilderTest {

  @Before
  public void before() throws IOException {
    Files.deleteIfExists(TestResults.getSerFilePath());
  }

  @Test
  public void testStart01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final Path workPath = rootPath.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workPath);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults result = builder.start(variant.getGeneratedSourceCode());

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
    final Path workPath = rootPath.resolve("bin");

    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workPath);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

    assertThat(r).isInstanceOf(EmptyTestResults.class);
    assertThat(r.getExecutedTestFQNs()).isEmpty();
    assertThat(r.getSuccessedTestResults()).isEmpty();
    assertThat(r.getFailedTestResults()).isEmpty();
    assertThat(r.getSuccessRate()).isNaN();
  }

}
