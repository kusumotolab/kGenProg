package jp.kusumotolab.kgenprog.output;

import static jp.kusumotolab.kgenprog.project.factory.JUnitLibraryResolver.JUnitVersion.JUNIT4;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import com.google.gson.Gson;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class TestResultsSerializerTest {

  private final Gson gson = TestUtil.createGson();

  private TestResults execTest(final Path rootPath, final List<Path> sourcePaths,
      final List<Path> testPaths) {
    final TargetProject targetProject = TargetProjectFactory.create(rootPath, sourcePaths,
        testPaths, Collections.emptyList(), JUNIT4);
    final GeneratedSourceCode generatedSourceCode = TestUtil.createGeneratedSourceCode(
        targetProject);
    final Configuration config = new Configuration.Builder(targetProject)
        .build();
    final TestExecutor executor = new LocalTestExecutor(config);
    final Variant variant = mock(Variant.class);
    when(variant.getGeneratedSourceCode()).thenReturn(generatedSourceCode);
    return executor.exec(variant);
  }

  /**
   * ビルドに失敗したプロジェクトのテスト結果を正しくシリアライズできるかテスト
   */
  @Test
  public void testSerializedTestResultsOnBuildFail() {
    // テストの実行
    final Path rootPath = Paths.get("example/BuildFailure01");
    final Path sourcePath = rootPath.resolve("src");
    final TestResults result = execTest(rootPath, Collections.singletonList(sourcePath),
        Collections.emptyList());

    // シリアライズ
    final String serializedTestResults = gson.toJson(result);

    // 他のキーを持っていないかチェック
    assertThatJson(serializedTestResults)
        .isObject()
        .containsOnlyKeys(JsonKeyAlias.TestResults.EXECUTED_TESTS_COUNT,
            JsonKeyAlias.TestResults.TEST_RESULTS,
            JsonKeyAlias.TestResults.SUCCESS_RATE);

    // 値が正しいかチェック
    assertThatJson(serializedTestResults)
        .node(JsonKeyAlias.TestResults.EXECUTED_TESTS_COUNT)
        .isEqualTo(0);
    assertThatJson(serializedTestResults)
        .node(JsonKeyAlias.TestResults.TEST_RESULTS)
        .isArray()
        .isEmpty();
    assertThatJson(serializedTestResults)
        .node(JsonKeyAlias.TestResults.SUCCESS_RATE)
        .isEqualTo(-1.0d);
  }

  /**
   * ビルドに成功したプロジェクトのテスト結果を正しくシリアライズできるかテスト
   */
  @Test
  public void testSerializedTestResultsOnBuildSuccess() {
    // テストの実行
    final Path rootPath = Paths.get("example/CloseToZero01");
    final Path sourcePath = rootPath.resolve("src/example/CloseToZero.java");
    final Path testPath = rootPath.resolve("src/example/CloseToZeroTest.java");

    final TestResults result = execTest(rootPath, Collections.singletonList(sourcePath),
        Collections.singletonList(testPath));

    // シリアライズ
    final String serializedTestResults = gson.toJson(result);

    // 他のキーを持っていないかチェック
    assertThatJson(serializedTestResults)
        .isObject()
        .containsOnlyKeys(JsonKeyAlias.TestResults.EXECUTED_TESTS_COUNT,
            JsonKeyAlias.TestResults.TEST_RESULTS,
            JsonKeyAlias.TestResults.SUCCESS_RATE);

    // 値が正しいかチェック
    assertThatJson(serializedTestResults)
        .node(JsonKeyAlias.TestResults.EXECUTED_TESTS_COUNT)
        .isEqualTo(4);
    assertThatJson(serializedTestResults)
        .node(JsonKeyAlias.TestResults.TEST_RESULTS)
        .isArray()
        .hasSize(4);
    assertThatJson(serializedTestResults)
        .node(JsonKeyAlias.TestResults.SUCCESS_RATE)
        .isEqualTo(0.75d);

    assertThatJson(serializedTestResults)
        .inPath("$.testResults[*].fqn")
        .isArray()
        .containsExactly("example.CloseToZeroTest.test01",
            "example.CloseToZeroTest.test02",
            "example.CloseToZeroTest.test03",
            "example.CloseToZeroTest.test04");
    assertThatJson(serializedTestResults)
        .inPath("$.testResults[*].isSuccess")
        .isArray()
        .containsExactly(true, true, false, true);
  }
}
