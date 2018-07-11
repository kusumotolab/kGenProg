package jp.kusumotolab.kgenprog.project.test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.BuildResults;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTLocation;

public class TestResultsTest {

  final static FullyQualifiedName buggyCalculator =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator");
  final static FullyQualifiedName buggyCalculatorTest =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest");

  final static FullyQualifiedName test01 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
  final static FullyQualifiedName test02 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
  final static FullyQualifiedName test03 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
  final static FullyQualifiedName test04 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");

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

  /**
   * FLで用いる4メトリクスのテスト
   */
  @Test
  public void checkFLMetricsInTestResultsForExample01() throws Exception {
    // final TestResults r = generateTestResultsForExample01();
    // final FullyQualifiedName bc = buggyCalculator; // alias for buggycalculator
    //
    // // example01でのbcの6行目（n++;）のテスト結果はこうなるはず
    // assertThat(r.getPassedTestFQNsExecutingTheStatement(bc, 5),
    // is(containsInAnyOrder(test01, test02)));
    // assertThat(r.getFailedTestFQNsExecutingTheStatement(bc, 5), is(empty()));
    // assertThat(r.getPassedTestFQNsNotExecutingTheStatement(bc, 5),
    // is(containsInAnyOrder(test04)));
    // assertThat(r.getFailedTestFQNsNotExecutingTheStatement(bc, 5),
    // is(containsInAnyOrder(test03)));
    //
    // // example01でのbcの10行目（return n;）のテスト結果はこうなるはず
    // assertThat(r.getPassedTestFQNsExecutingTheStatement(bc, 10),
    // is(containsInAnyOrder(test01, test02, test04)));
    // assertThat(r.getFailedTestFQNsExecutingTheStatement(bc, 10), is(containsInAnyOrder(test03)));
    // assertThat(r.getPassedTestFQNsNotExecutingTheStatement(bc, 10), is(empty()));
    // assertThat(r.getFailedTestFQNsNotExecutingTheStatement(bc, 10), is(empty()));
  }

  /**
   * FLで用いる4メトリクスのテスト
   */
  @Test
  public void checkFLMetricsInTestResultsForExample02() throws Exception {
    // actual確保のためにテストの実行
    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProjectFactory.create(rootDir);
    final GeneratedSourceCode generatedSourceCode =
        targetProject.getInitialVariant().getGeneratedSourceCode();
    final BuildResults buildResults =
        new ProjectBuilder(targetProject).build(generatedSourceCode, outDir);
    final TestExecutor executor = new TestExecutor(new URL[] {outDir.toUri().toURL()});
    final TestResults testResults =
        executor.exec(Arrays.asList(buggyCalculator), Arrays.asList(buggyCalculatorTest));
    testResults.setBuildResults(buildResults);

    // expected確保の作業
    // まずast生成
    final Path bcSourcePath = Paths.get(buggyCalculator.value.replaceAll("\\.", "/") + ".java");
    final TargetSourceFile bcSourceFile =
        new TargetSourceFile(rootDir.resolve("src").resolve(bcSourcePath));
    final GeneratedJDTAST bcAst = (GeneratedJDTAST) generatedSourceCode.getAST(bcSourceFile);

    // astから5行目 (n--;) のlocationを取り出す
    final List<Location> locations1 = bcAst.inferLocations(5);
    final Location location1 = locations1.get(locations1.size() - 1);
    final JDTLocation jdtLocation1 = (JDTLocation) location1;

    // 一応locationの中身を確認しておく
    assertThat(jdtLocation1.node.toString(), is("n--;\n"));

    // 4メトリクスの取り出しとassertion
    final long a_ep1 =
        testResults.getNumberOfPassedTestsExecutingTheStatement(bcSourceFile, location1);
    final long a_ef1 =
        testResults.getNumberOfFailedTestsExecutingTheStatement(bcSourceFile, location1);
    final long a_np1 =
        testResults.getNumberOfPassedTestsNotExecutingTheStatement(bcSourceFile, location1);
    final long a_nf1 =
        testResults.getNumberOfFailedTestsNotExecutingTheStatement(bcSourceFile, location1);
    assertThat(a_ep1, is(2L)); // test01, test02
    assertThat(a_ef1, is(0L));
    assertThat(a_np1, is(1L)); // test04
    assertThat(a_nf1, is(1L)); // test03


    // astから10行目 (return n;) のlocationを取り出す
    final List<Location> locations2 = bcAst.inferLocations(10);
    final Location location2 = locations2.get(locations2.size() - 1);
    final JDTLocation jdtLocation2 = (JDTLocation) location2;

    // 一応locationの中身を確認しておく
    assertThat(jdtLocation2.node.toString(), is("return n;\n"));

    // 4メトリクスの取り出しとassertion
    final long a_ep2 =
        testResults.getNumberOfPassedTestsExecutingTheStatement(bcSourceFile, location2);
    final long a_ef2 =
        testResults.getNumberOfFailedTestsExecutingTheStatement(bcSourceFile, location2);
    final long a_np2 =
        testResults.getNumberOfPassedTestsNotExecutingTheStatement(bcSourceFile, location2);
    final long a_nf2 =
        testResults.getNumberOfFailedTestsNotExecutingTheStatement(bcSourceFile, location2);
    assertThat(a_ep2, is(3L)); // test01, test02, test04
    assertThat(a_ef2, is(1L)); // test03
    assertThat(a_np2, is(0L));
    assertThat(a_nf2, is(0L));
  }

  /**
   * toString()のテスト．JSON形式の確認
   */
  @Test
  public void testToString() throws Exception {
    final TestResults r = generateTestResultsForExample01();
    final String expected = "" //
        + "[\n" //
        + "  {\n" //
        + "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test04\",\n" //
        + "    \"wasFailed\": false,\n" //
        + "    \"coverages\": [\n" //
        + "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 2, 0, 2, 1, 0, 0, 2, 0, 2]}\n" //
        + "    ]\n" //
        + "  },\n" //
        + "  {\n" //
        + "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test03\",\n" //
        + "    \"wasFailed\": true,\n" //
        + "    \"coverages\": [\n" //
        + "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 2, 0, 2, 1, 0, 0, 2, 0, 2]}\n" //
        + "    ]\n" //
        + "  },\n" //
        + "  {\n" //
        + "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test02\",\n" //
        + "    \"wasFailed\": false,\n" //
        + "    \"coverages\": [\n" //
        + "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 2, 0, 2, 2, 0, 0, 1, 0, 2]}\n" //
        + "    ]\n" //
        + "  },\n" //
        + "  {\n" //
        + "    \"executedTestFQN\": \"jp.kusumotolab.BuggyCalculatorTest.test01\",\n" //
        + "    \"wasFailed\": false,\n" //
        + "    \"coverages\": [\n" //
        + "      {\"executedTargetFQN\": \"jp.kusumotolab.BuggyCalculator\", \"coverages\": [0, 2, 0, 2, 2, 0, 0, 1, 0, 2]}\n" //
        + "    ]\n" //
        + "  }\n" //
        + "]\n";

    assertThat(normalizeCrLf(r.toString()), is(normalizeCrLf(expected)));
  }

  /**
   * 単純なserialize -> deserializeの確認
   */
  @Test
  public void testSerializeDeserialize01() throws Exception {
    final TestResults r1 = new TestResults();

    // ダミーな内部要素を追加
    r1.add(new TestResult(test01, false, new HashMap<>()));
    r1.add(new TestResult(test03, false, new HashMap<>()));

    // serializeして
    TestResults.serialize(r1);

    // ファイルが存在するはず
    assertThat(TestResults.getSerFilePath().toFile().exists(), is(true));

    // deserializeして
    final TestResults r2 = TestResults.deserialize();

    // 要素が正しいはず
    assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test03)));
  }

  /**
   * serialize -> 要素書き換え -> deserializeの確認
   */
  @Test
  public void testSerializeDeserialize02() throws Exception {
    final TestResults r1 = new TestResults();

    // ダミーな内部要素を追加
    r1.add(new TestResult(test01, false, new HashMap<>()));

    // serializeして
    TestResults.serialize(r1);

    // serialize後に03を追加
    r1.add(new TestResult(test03, false, new HashMap<>()));

    // deserializeして
    final TestResults r2 = TestResults.deserialize();

    // 要素が正しいはず
    assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01)));
  }

  /**
   * serialize -> serialize -> deserializeの確認
   */
  @Test
  public void testSerializeDeserialize03() throws Exception {
    final TestResults r1 = new TestResults();

    // ダミーな内部要素を追加
    r1.add(new TestResult(test01, false, new HashMap<>()));

    // serializeして
    TestResults.serialize(r1);

    // serialize後に03を追加
    r1.add(new TestResult(test03, false, new HashMap<>()));

    // serializeして
    TestResults.serialize(r1);

    // deserializeして
    final TestResults r2 = TestResults.deserialize();

    // 要素が正しいはず
    assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test03)));
  }

  /**
   * 重複要素を持つ要素のserialize -> deserializeの確認
   */
  @Test
  public void testSerializeDeserialize04() throws Exception {
    final TestResults r1 = new TestResults();

    // ダミーな内部要素を追加（重複するtest03を追加）
    r1.add(new TestResult(test01, false, new HashMap<>()));
    r1.add(new TestResult(test03, false, new HashMap<>()));
    r1.add(new TestResult(test03, false, new HashMap<>()));
    r1.add(new TestResult(test03, false, new HashMap<>()));

    // serializeして
    TestResults.serialize(r1);

    // ファイルが存在するはず
    assertThat(TestResults.getSerFilePath().toFile().exists(), is(true));

    // deserializeして
    final TestResults r2 = TestResults.deserialize();

    // 要素が正しいはず
    assertThat(r2.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test03)));
  }

  /**
   * いきなりdeserializeした際の確認
   */
  @Test(expected = NoSuchFileException.class)
  public void testSerializeDeserialize05() throws Exception {
    // serializeファイルを消しておいて
    TestResults.getSerFilePath().toFile().delete();

    // deserializeでNoSuchFileExceptionが返ってくるはず
    TestResults.deserialize();
  }

  /**
   * 改行コードのnormalizer
   * 
   * @param s
   * @return
   */
  private String normalizeCrLf(final String s) {
    return s.replaceAll("\\r|\\n", "\n");
  }
}
