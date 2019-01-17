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
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST01;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST02;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST03;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO_TEST04;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class LocalTestExecutorTest {

  @Test
  // カスタムjunitがロードされているかテスト
  public void testForJunitVersion() {
    assertThat(junit.runner.Version.id()).isEqualTo("4.12-kgp-custom");
  }

  @Test
  // 正常系題材の確認
  public void testExecForBuildSuccess01() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

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
  // 正常系題材の確認
  public void testExecForBuildSuccess02() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

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
  // そもそもビルドできない題材の確認
  public void testExecForBuildFailure01() {
    final Path rootPath = Paths.get("example/BuildFailure01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    // 実行されたテストはないはず
    assertThat(result).isInstanceOf(EmptyTestResults.class);
    assertThat(result.getExecutedTestFQNs()).isEmpty();
  }

  @Test
  // 内部クラスを持つ題材に対するFLメトリクスの確認
  public void testExecWithRetrievingFLParametersWithInnerClass() {
    final Path rootPath = Paths.get("example/BuildSuccess03");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    // 内部クラスを持つBazのASTと，Baz#OuterClassのL66のASTLocationを取り出す
    final ProductSourcePath baz = new ProductSourcePath(rootPath, Src.BAZ);
    final ASTLocation loc1 = source.getProductAst(baz)
        .createLocations()
        .getAll()
        .get(23); // L66 in OuterClass. "new String()" statement;

    // 一応確認．66行目のはず
    assertThat(loc1.inferLineNumbers()).isEqualTo(new LineNumberRange(66, 66));

    final long ep1 = result.getNumberOfPassedTestsExecutingTheStatement(baz, loc1);
    final long np1 = result.getNumberOfPassedTestsNotExecutingTheStatement(baz, loc1);
    final long ef1 = result.getNumberOfFailedTestsExecutingTheStatement(baz, loc1);
    final long nf1 = result.getNumberOfFailedTestsNotExecutingTheStatement(baz, loc1);
    assertThat(ep1).isEqualTo(2); // BazTest#test01 & BazTest#test02
    assertThat(np1).isEqualTo(8); // FooTest#testXX(3個) & BarTest#testXX(5個)
    assertThat(ef1).isEqualTo(0);
    assertThat(nf1).isEqualTo(1); // FooTest#test03

    // Baz#InnerClassのL66のASTLocationを取り出す
    final ASTLocation loc2 = source.getProductAst(baz)
        .createLocations()
        .getAll()
        .get(19); // L49 in OuterClass. "new String()" statement;

    // 一応確認．49行目のはず
    assertThat(loc2.inferLineNumbers()).isEqualTo(new LineNumberRange(49, 49));

    final long ep2 = result.getNumberOfPassedTestsExecutingTheStatement(baz, loc2);
    final long np2 = result.getNumberOfPassedTestsNotExecutingTheStatement(baz, loc2);
    final long ef2 = result.getNumberOfFailedTestsExecutingTheStatement(baz, loc2);
    final long nf2 = result.getNumberOfFailedTestsNotExecutingTheStatement(baz, loc2);
    assertThat(ep2).isEqualTo(1); // BazTest#test01
    assertThat(np2).isEqualTo(9); // BazTest#test02 & FooTest#testXX(3個) & BarTest#testXX(5個)
    assertThat(ef2).isEqualTo(0);
    assertThat(nf2).isEqualTo(1); // FooTest#test03
  }

  @Test
  // 実行テスト指定の確認
  public void testExecWithSpecifyingExecutionTest() {
    final Path rootPath = Paths.get("example/BuildSuccess02");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    // 実行するべきテストをFooTestのみに変更 （BarTestを実行しない）
    final Configuration config = new Configuration.Builder(targetProject) //
        .addExecutionTest("example.FooTest")
        .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    // 実行されたテストは10個から4個に減ったはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);
    // こっちは実行されない
    // BarTest01, BarTest02, BarTest03, BarTest04, BarTest05);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();

    final TestResult fooTest01result = result.getTestResult(FOO_TEST01);

    // FooTest.test01()ではFooとBarが実行されたはず
    assertThat(fooTest01result.getExecutedTargetFQNs()).containsExactlyInAnyOrder(FOO, BAR);

    // FooTest.test01()で実行されたFooのカバレッジはこうなるはず
    assertThat(fooTest01result.getCoverages(FOO).statuses).containsExactlyInAnyOrder(EMPTY, COVERED,
        EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED);
  }

  @Test
  // 実行テスト名の指定ミスの確認
  public void testExecWithSpecifyingMissingExecutionTest() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject) //
        .addExecutionTest("example.FooTestXXXXXXXX") // no such method
        .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    // 実行されたテストはないはず
    assertThat(result).isInstanceOf(EmptyTestResults.class);
    assertThat(result.getExecutedTestFQNs()).isEmpty();
  }

  @Test
  // テストの途中でクラスロードを要する題材の確認
  public void testExecForClassLoadingDuringTestExecution() {
    final Path rootPath = Paths.get("example/BuildSuccess10");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

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
  // 継承ベースの古いJUnitテストを試す題材の確認
  public void testExecForExtendBasedTestCase() {
    final Path rootPath = Paths.get("example/BuildSuccess11");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

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
  // テスト内で別テスト系クラス（ユーティリティ等）に依存する題材の確認
  public void testExecWithUtilityDependentTestCase() {
    final Path rootPath = Paths.get("example/BuildSuccess12");
    final List<Path> productPaths = Arrays.asList(rootPath.resolve("src"));
    final List<Path> testPaths = Arrays.asList(rootPath.resolve("test"));
    final TargetProject targetProject = TargetProjectFactory.create(rootPath, productPaths,
        testPaths, Collections.emptyList(), JUnitVersion.JUNIT4);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject) //
        .addExecutionTest(FOO_TEST.value) // FooTestのみ実行する（非依存テストは実行しない）
        .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    // 実行されたテストは4個のはず（BarTest#test01は実行されない）
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();
  }

  @Test
  // 正常系題材の確認
  public void testExecForBuildSuccessWithMultipleTestExecution() {
    final Path rootPath = Paths.get("example/BuildSuccess14");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant1 = mock(Variant.class);
    when(variant1.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result1 = executor.exec(variant1);

    // 実行されたテストは4個のはず
    assertThat(result1.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 一つバグるはず
    assertThat(result1.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result1.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result1.getTestResult(FOO_TEST03).failed).isTrue();
    assertThat(result1.getTestResult(FOO_TEST04).failed).isFalse();

    // FooのASTを取り出す
    final ProductSourcePath fooPath = new ProductSourcePath(rootPath, Src.FOO);
    final GeneratedAST<?> ast = source.getProductAst(fooPath);

    // バグ箇所を取り出す（7行目のはず）
    final ASTLocation location = ast.createLocations()
        .getAll()
        .get(3);
    assertThat(location.inferLineNumbers().start).isSameAs(7);

    // バグ箇所を削除
    final DeleteOperation dop = new DeleteOperation();
    final GeneratedSourceCode source2 = dop.apply(source, location);

    // 再度テスト実行
    final Variant variant2 = mock(Variant.class);
    when(variant2.getGeneratedSourceCode()).thenReturn(source2);
    final TestResults result2 = executor.exec(variant2);

    // 実行されたテストは4個のはず
    assertThat(result2.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 全て成功するはず
    assertThat(result2.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result2.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result2.getTestResult(FOO_TEST03).failed).isFalse();
    assertThat(result2.getTestResult(FOO_TEST04).failed).isFalse();
  }

  @Test
  // テスト内で外部libを参照する題材の確認
  public void testExecForBuildSuccessWithExternalLibLoading() {
    final Path rootPath = Paths.get("example/BuildSuccess16");
    final List<Path> sources = Arrays.asList(rootPath.resolve("src"));
    final List<Path> tests = Arrays.asList(rootPath.resolve("test"));
    final List<Path> cps = Arrays.asList(//
        rootPath.resolve("lib1"), // .classを含むlibへのパス
        rootPath.resolve("lib2/Baz.jar") // .jarを含むlibへのパス
    );

    final TargetProject targetProject =
        TargetProjectFactory.create(rootPath, sources, tests, cps, JUnitVersion.JUNIT4);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    // 実行されたテストは4個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03, FOO_TEST04);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST03).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST04).failed).isFalse();
  }

  @Test
  // テスト内でファイル読み込みがある題材の確認
  public void testExecWithTestCaseIncludeFileInput() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess17");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);

    // 現在のworking-dirを擬似的に対象プロジェクトに移動する．別プロセス切り出しが難しいため
    final String userDir = System.getProperty("user.dir");
    System.setProperty("user.dir", rootPath.toAbsolutePath()
        .toString());

    final TestResults result = executor.exec(variant);

    // 実行されたテストは1個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder(FOO_TEST01);

    // user.dir を戻しておく（副作用回避）
    System.setProperty("user.dir", userDir);

    // テストは成功するはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
  }

  @Test
  // テスト内でファイル書き込みがある題材の確認
  public void testExecWithTestCaseIncludeFileOutput() throws Exception {
    final Path rootPath = Paths.get("example/BuildSuccess18");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);

    // 現在のworking-dirを擬似的に対象プロジェクトに移動する．別プロセス切り出しが難しいため
    final String userDir = System.getProperty("user.dir");
    System.setProperty("user.dir", rootPath.toAbsolutePath()
        .toString());

    final TestResults result = executor.exec(variant);

    // 実行されたテストは1個のはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder(FOO_TEST01);

    // user.dir を戻しておく（副作用回避）
    System.setProperty("user.dir", userDir);

    // テストは成功するはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
  }

  @Test
  // 無限ループする題材の確認 （より詳細なテストは02参照）
  public void testExecForInfiniteLoop01() {
    final Path rootPath = Paths.get("example/BuildSuccess04");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject) //
        .setTestTimeLimitSeconds(1) // タイムアウト時間を短めに設定（CI高速化のため）
        .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    // 無限ループが発生するが3つのテストが実行されるはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03);

    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();
  }

  @Test
  // 無限ループする題材の確認
  public void testExecForInfiniteLoop02() {
    final Path rootPath = Paths.get("example/BuildSuccess19");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject) //
        .setTestTimeLimitSeconds(1) // タイムアウト時間を短めに設定（CI高速化のため）
        .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);

    // スレッド打ち切り確認のために標準出力と標準エラーを捕まえておく
    final PrintStream origStdout = System.out;
    final OutputStream stdout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(stdout));

    // TODO 標準エラーが握りつぶされてしまうためひとまずコメントアウト．
    // 標準エラー上で "interrupted" を期待するようなテストが題材に含まれる場合，バグる可能性高いので注意．
    // final PrintStream origStderr = System.err;
    // final OutputStream stderr = new ByteArrayOutputStream();
    // System.setErr(new PrintStream(stderr));

    // テスト実行
    final TestResults result = executor.exec(variant);

    // 標準出力と標準エラーを戻しておく
    System.setOut(origStdout);
    // System.setErr(origStderr);

    // TEST01で無限ループが発生するが，続く残りの2個のテストも実行されるはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();

    // スレッド打ち切りの確認：

    // 各テスト名は3回出力されるはず．スレッドが生き残ると3回以上の出力される （300ms / 1000ms = 3）
    // TODO 割り込みタイミングによって結果が揺れる可能性あり．
    assertThat(countPattern(stdout.toString(), FOO_TEST02.value)).isSameAs(3);
    assertThat(countPattern(stdout.toString(), FOO_TEST03.value)).isSameAs(3);

    // 標準エラーには割り込み例外が2回出力されるはず （握りつぶされるのでコメントアウト）
    // assertThat(countPattern(stderr.toString(), "sleep interrupted")).isSameAs(2);
  }

  @Test
  // 無限ループする題材の確認 （継承ベース）
  public void testExecForInfiniteLoopWithExtendBased() {
    final Path rootPath = Paths.get("example/BuildSuccess20");
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject) //
        .setTestTimeLimitSeconds(1) // タイムアウト時間を短めに設定（CI高速化のため）
        .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);

    // スレッド打ち切り確認のために標準出力と標準エラーを捕まえておく
    final PrintStream origStdout = System.out;
    final OutputStream stdout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(stdout));

    // TODO 標準エラーが握りつぶされてしまうためひとまずコメントアウト．
    // 標準エラー上で "interrupted" を期待するようなテストが題材に含まれる場合，バグる可能性高いので注意．
    // final PrintStream origStderr = System.err;
    // final OutputStream stderr = new ByteArrayOutputStream();
    // System.setErr(new PrintStream(stderr));

    // テスト実行
    final TestResults result = executor.exec(variant);

    // 標準出力と標準エラーを戻しておく
    System.setOut(origStdout);
    // System.setErr(origStderr);

    // TEST01で無限ループが発生するが，続く残りの2個のテストも実行されるはず
    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        FOO_TEST01, FOO_TEST02, FOO_TEST03);

    // 全テストの成否はこうなるはず
    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isTrue();
    assertThat(result.getTestResult(FOO_TEST03).failed).isTrue();

    // スレッド打ち切りの確認：

    // 各テスト名は3回出力されるはず．スレッドが生き残ると3回以上の出力される （300ms / 1000ms = 3）
    // TODO 割り込みタイミングによって結果が揺れる可能性あり．
    assertThat(countPattern(stdout.toString(), FOO_TEST02.value)).isSameAs(3);
    assertThat(countPattern(stdout.toString(), FOO_TEST03.value)).isSameAs(3);

    // 標準エラーには割り込み例外が2回出力されるはず （握りつぶされるのでコメントアウト）
    // assertThat(countPattern(stderr.toString(), "sleep interrupted")).isSameAs(2);
  }

  private int countPattern(final String str, final String substr) {
    final Pattern pattern = Pattern.compile(substr);
    final Matcher matcher = pattern.matcher(str);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    return count;
  }

  @Test
  // resources内のファイルにアクセスする題材の確認
  public void testExecForResourceAccess() {
    final Path rootPath = Paths.get("example/BuildSuccess21");

    final List<Path> sources = Arrays.asList(rootPath.resolve("src"));
    final List<Path> tests = Arrays.asList(rootPath.resolve("test"));
    final List<Path> cps = Arrays.asList(rootPath.resolve("resources"));
    final TargetProject targetProject =
        TargetProjectFactory.create(rootPath, sources, tests, cps, JUnitVersion.JUNIT4);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    final FullyQualifiedName test01 = new TargetFullyQualifiedName("example.FooTest.test01");
    final FullyQualifiedName test02 = new TargetFullyQualifiedName("example.FooTest.test02");
    final FullyQualifiedName test03 = new TargetFullyQualifiedName("example.FooTest.test03");
    final FullyQualifiedName test04 = new TargetFullyQualifiedName("example.FooTest.test04");
    final FullyQualifiedName test11 = new TargetFullyQualifiedName("example.FooTest.test11");
    final FullyQualifiedName test12 = new TargetFullyQualifiedName("example.FooTest.test12");
    final FullyQualifiedName test13 = new TargetFullyQualifiedName("example.FooTest.test13");
    final FullyQualifiedName test14 = new TargetFullyQualifiedName("example.FooTest.test14");

    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder( //
        test01, test02, test03, test04, test11, test12, test13, test14);

    assertThat(result.getTestResult(test01).failed).isFalse();
    assertThat(result.getTestResult(test02).failed).isFalse();
    assertThat(result.getTestResult(test03).failed).isFalse();
    assertThat(result.getTestResult(test04).failed).isFalse();
    assertThat(result.getTestResult(test11).failed).isFalse();
    assertThat(result.getTestResult(test12).failed).isFalse();
    assertThat(result.getTestResult(test13).failed).isFalse();
    assertThat(result.getTestResult(test14).failed).isFalse();
  }

  @Test
  // クラスローダが抱えるバイナリ形式のresourcesにアクセスする題材の確認
  public void testExecForResourceAcessFromClassLoader() {
    final Path rootPath = Paths.get("example/BuildSuccess22");

    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode source = TestUtil.createGeneratedSourceCode(targetProject);

    final Configuration config = new Configuration.Builder(targetProject).build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(source);
    final TestResults result = executor.exec(variant);

    assertThat(result.getExecutedTestFQNs()).containsExactlyInAnyOrder(FOO_TEST01, FOO_TEST02);

    assertThat(result.getTestResult(FOO_TEST01).failed).isFalse();
    assertThat(result.getTestResult(FOO_TEST02).failed).isFalse();
  }
}
