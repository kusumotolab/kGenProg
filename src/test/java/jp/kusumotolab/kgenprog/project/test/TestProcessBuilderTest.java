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
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;

public class TestProcessBuilderTest {

  final static TestFullyQualifiedName test01 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
  final static TestFullyQualifiedName test02 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
  final static TestFullyQualifiedName test03 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
  final static TestFullyQualifiedName test04 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");
  final static TargetFullyQualifiedName buggyCalculator =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator");

  @Before
  public void before() throws IOException {
    Files.deleteIfExists(TestResults.getSerFilePath());
  }

  @Test
  public void testStart01() {
    final Path rootDir = Paths.get("example/example01");
    final Path workingDir = rootDir.resolve("bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workingDir);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

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
  public void testStartWithOtherWorkingDir01() {
    final Path rootDir = Paths.get("example/example01");

    // exampleとは全く別のworkingDirで動作確認
    final Path workingDir = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-tmp");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workingDir);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs()).hasSize(4);
  }

  @Test
  public void testStartWithOtherWorkingDir02() {
    // 絶対パスにしてみる
    final Path rootDir = Paths.get("example/example01")
        .toAbsolutePath();

    // exampleとは全く別のworkingDirで動作確認
    final Path workingDir = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-tmp");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workingDir);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs()).hasSize(4);
  }

  @Test
  public void testBuildFailure01() throws IOException {
    final Path rootDir = Paths.get("example/example00");
    final Path outDir = rootDir.resolve("bin");

    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);
    final Variant variant = targetProject.getInitialVariant();
    final TestResults r = builder.start(variant.getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs()).isEmpty();
    assertThat(r.getSuccessedTestResults()).isEmpty();
    assertThat(r.getFailedTestResults()).isEmpty();
    assertThat(r.getSuccessRate()).isNaN();
  }

}
