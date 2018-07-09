package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
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
    TestResults.getSerFilePath().toFile().delete();
  }

  @Test
  public void testStart01() {
    final Path rootDir = Paths.get("example/example01");
    final Path workingDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workingDir);
    final TestResults r = builder.start(targetProject.getInitialVariant().getGeneratedSourceCode());

    // テストの結果はこうなるはず
    assertThat(r.getExecutedTestFQNs().size(), is(4));
    assertThat(r.getSuccessRate(), is(1.0 * 3 / 4));
    assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04)));
    assertThat(r.getTestResult(test01).failed, is(false));
    assertThat(r.getTestResult(test02).failed, is(false));
    assertThat(r.getTestResult(test03).failed, is(true));
    assertThat(r.getTestResult(test04).failed, is(false));

    // BuggyCalculatorTest.test01 実行によるbuggyCalculatorのカバレッジはこうなるはず
    assertThat(r.getTestResult(test01).getCoverages(buggyCalculator).statuses, is(contains(EMPTY,
        COVERED, EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED)));

    // BuggyCalculatorTest.test04 実行によるbuggyCalculatorのバレッジはこうなるはず
    assertThat(r.getTestResult(test04).getCoverages(buggyCalculator).statuses, is(contains(EMPTY,
        COVERED, EMPTY, COVERED, NOT_COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED)));
  }

  @Test
  public void testStartWithOtherWorkingDir01() {
    final Path rootDir = Paths.get("example/example01");

    // exampleとは全く別のworkingDirで動作確認
    final Path workingDir = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-tmp");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workingDir);
    final TestResults r = builder.start(targetProject.getInitialVariant().getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs().size(), is(4));
  }

  @Test
  public void testStartWithOtherWorkingDir02() {
    // 絶対パスにしてみる
    final Path rootDir = Paths.get("example/example01").toAbsolutePath();

    // exampleとは全く別のworkingDirで動作確認
    final Path workingDir = Paths.get(System.getProperty("java.io.tmpdir"), "kgenprog-tmp");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, workingDir);
    final TestResults r = builder.start(targetProject.getInitialVariant().getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs().size(), is(4));
  }

  @Test
  public void testBuildFailure01() throws IOException {
    final Path rootDir = Paths.get("example/example00");

    // TODO 一時的なSyserr対策．
    // そもそもコンパイルエラー時にsyserr吐かないほうが良い．
    final PrintStream ps = System.err;
    System.setErr(new PrintStream(new OutputStream() {
      @Override
      public void write(int b) {} // 何もしないwriter
    }));

    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);

    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);
    final TestResults r = builder.start(targetProject.getInitialVariant().getGeneratedSourceCode());

    assertThat(r.getExecutedTestFQNs(), is(empty()));
    assertThat(r.getSuccessedTestResults(), is(empty()));
    assertThat(r.getFailedTestResults(), is(empty()));
    assertThat(r.getSuccessRate(), is(Double.NaN));

    // 後処理
    System.setErr(ps);
  }


}
