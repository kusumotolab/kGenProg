package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR_TEST01;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR_TEST02;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR_TEST03;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR_TEST04;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR_TEST05;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST01;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST02;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST03;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST04;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class TestExecutorTest {

  private final static long TIMEOUT_SEC = 60;

  @Before
  public void before() throws IOException {}

  @After
  public void after() throws IOException {}

  @Test
  public void testExecForBuildSuccess01() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 実行されたテストは4個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();

    // よってテストの成功率はこうなる
    assertThat(result.getSuccessRate()).isEqualTo(1.0 * 3 / 4);

    final TestResult fooTest01result = result.getTestResult(FOO_TEST01);
    final TestResult fooTest04result = result.getTestResult(FOO_TEST04);

    // FooTest.test01 実行によるFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(FOO).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // FooTest.test04 実行によるFooのバレッジはこうなるはず
    assertThat(fooTest04result.getCoverages(FOO).statuses).containsExactly(EMPTY, COVERED, EMPTY,
        COVERED, NOT_COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED);
  }

  @Test
  public void testExecForBuildSuccess02() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 実行されたテストは10個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04, //
        BAR_TEST01, BAR_TEST02, BAR_TEST03, BAR_TEST04, BAR_TEST05);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();
    assertThat(result.getTestResult(BAR_TEST01).failed).isFalse();
    assertThat(result.getTestResult(BAR_TEST02).failed).isFalse();
    assertThat(result.getTestResult(BAR_TEST03).failed).isFalse();
    assertThat(result.getTestResult(BAR_TEST04).failed).isFalse();
    assertThat(result.getTestResult(BAR_TEST05).failed).isFalse();

    final TestResult fooTest01result = result.getTestResult(FOO_TEST01);
    // FooTest.test01()ではFooとBarが実行されたはず
    assertThat(fooTest01result.getExecutedTargetFQNs()).containsExactlyInAnyOrder(FOO, BAR);

    // FooTest.test01()で実行されたFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(FOO).statuses).containsExactlyInAnyOrder(EMPTY, COVERED,
        EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);

    // BarTest.test01()ではFooとBarが実行されたはず
    final TestResult barTest01r = result.getTestResult(BAR_TEST01);
    assertThat(barTest01r.getExecutedTargetFQNs()).containsExactlyInAnyOrder(FOO, BAR);

    // BarTest.test01()で実行されたBarのカバレッジはこうなるはず
    assertThat(barTest01r.getCoverages(BAR).statuses).containsExactlyInAnyOrder(EMPTY, NOT_COVERED,
        EMPTY, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, EMPTY, NOT_COVERED, NOT_COVERED);

    // TODO 最後のNOT_COVERDだけ理解できない．謎．
  }

  @Test
  public void testRetrievingFLParametersWithInnerClass() throws Exception {
    // 内部クラスを持つ題材に対するFLメトリクスのテスト

    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 内部クラスを持つBazのASTと，Baz#OuterClassのL66のASTLocationを取り出す
    final ProductSourcePath baz = new ProductSourcePath(rootPath.resolve(Src.BAZ));
    final ASTLocation loc1 = generatedSourceCode.getAst(baz)
        .getAllLocations()
        .get(23); // L66 in OuterClass. "new String()" statement;

    // 一応確認．66行目のはず
    assertThat(loc1.inferLineNumbers()).isEqualTo(new LineNumberRange(66, 66));

    long ep1 = result.getNumberOfPassedTestsExecutingTheStatement(baz, loc1);
    long np1 = result.getNumberOfPassedTestsNotExecutingTheStatement(baz, loc1);
    long ef1 = result.getNumberOfFailedTestsExecutingTheStatement(baz, loc1);
    long nf1 = result.getNumberOfFailedTestsNotExecutingTheStatement(baz, loc1);
    assertThat(ep1).isEqualTo(2); // BazTest#test01 & BazTest#test02
    assertThat(np1).isEqualTo(8); // FooTest#testXX(3個) & BarTest#testXX(5個)
    assertThat(ef1).isEqualTo(0);
    assertThat(nf1).isEqualTo(1); // FooTest#test03


    // Baz#InnerClassのL66のASTLocationを取り出す
    final ASTLocation loc2 = generatedSourceCode.getAst(baz)
        .getAllLocations()
        .get(19); // L49 in OuterClass. "new String()" statement;

    // 一応確認．49行目のはず
    assertThat(loc2.inferLineNumbers()).isEqualTo(new LineNumberRange(49, 49));

    long ep2 = result.getNumberOfPassedTestsExecutingTheStatement(baz, loc2);
    long np2 = result.getNumberOfPassedTestsNotExecutingTheStatement(baz, loc2);
    long ef2 = result.getNumberOfFailedTestsExecutingTheStatement(baz, loc2);
    long nf2 = result.getNumberOfFailedTestsNotExecutingTheStatement(baz, loc2);
    assertThat(ep2).isEqualTo(1); // BazTest#test01
    assertThat(np2).isEqualTo(9); // BazTest#test02 & FooTest#testXX(3個) & BarTest#testXX(5個)
    assertThat(ef2).isEqualTo(0);
    assertThat(nf2).isEqualTo(1); // FooTest#test03
  }

  @Test
  public void testExecForInfiniteLoop() throws Exception {

    // 無限ループする題材
    final Path rootPath = Paths.get("example/BuildSuccess04");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    // タイムアウト時間を短めに設定（CI高速化のため）
    final long timeout = 1;
    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(timeout)
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 無限ループが発生し，タイムアウトで打ち切られてEmptyになるはず
    assertThat(result).isInstanceOf(EmptyTestResults.class);
  }

  @Test
  public void testExecWithSpecifyingExecutedTest() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    // 実行するべきテストをFooTestのみに変更 （BarTestを実行しない）
    final Configuration config =
        new Configuration.Builder(targetProject).addExecutionTest("example.FooTest")
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 実行されたテストは10個から4個に減ったはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);
    // BarTest01, BarTest02, BarTest03, BarTest04, BarTest05);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();
    // assertThat(result.getTestResult(BarTest01).failed).isFalse();
    // assertThat(result.getTestResult(BarTest02).failed).isFalse();
    // assertThat(result.getTestResult(BarTest03).failed).isFalse();
    // assertThat(result.getTestResult(BarTest04).failed).isFalse();
    // assertThat(result.getTestResult(BarTest05).failed).isFalse();

    final TestResult fooTest01result = result.getTestResult(FOO_TEST01);

    // FooTest.test01()ではFooとBarが実行されたはず
    assertThat(fooTest01result.getExecutedTargetFQNs()).containsExactlyInAnyOrder(FOO, BAR);

    // FooTest.test01()で実行されたFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(FOO).statuses).containsExactlyInAnyOrder(EMPTY, COVERED,
        EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);
  }

  @Test
  public void testTestExecutorForBuildSuccess10() throws Exception {

    // テストの途中でクラスロードを要する題材
    final Path rootPath = Paths.get("example/BuildSuccess10");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 実行されたテストは4個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse(); // ここで動的ロード．パスするはず
  }

  @Test
  public void testTestExecutorForBuildSuccess11() throws Exception {
    // 継承ベースの古いJUnitテストを試す題材．

    final Path rootPath = Paths.get("example/BuildSuccess11");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 実行されたテストは4個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();
  }

  @Test
  public void testTestExecutorForBuildSuccess12() throws Exception {
    // テスト内で別テスト系クラス（ユーティリティ等）に依存する題材の確認

    final Path rootPath = Paths.get("example/BuildSuccess12");
    final List<Path> srcPaths = Arrays.asList(rootPath.resolve("src"));
    final List<Path> testPaths = Arrays.asList(rootPath.resolve("test"));
    final TargetProject targetProject = TargetProjectFactory.create(rootPath, srcPaths, testPaths,
        Collections.emptyList(), JUnitVersion.JUNIT4);
    final GeneratedSourceCode generatedSourceCode =
        TestUtil.createGeneratedSourceCode(targetProject);
    final ProjectBuilder projectBuilder = new ProjectBuilder(targetProject);
    projectBuilder.build(generatedSourceCode);

    final Configuration config =
        new Configuration.Builder(targetProject).setTimeLimitSeconds(TIMEOUT_SEC)
            .addExecutionTest("example.FooTest") // FooTestのみ実行する（非依存テストは実行しない）
            .build();
    final TestExecutor executor = new TestExecutor(config);
    final TestResults result = executor.exec(generatedSourceCode);

    // 実行されたテストは4個のはず（BarTest#test01は実行されない扱い）
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();
  }
}
