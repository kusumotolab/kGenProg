package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class TestResultsSerializerTest {

  private Gson gson;

  @Before
  public void setup() {
    gson = new GsonBuilder().registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .create();
  }

  private TestResults execTest(final String root) {
    final Path rootPath = Paths.get(root);
    final TargetProject targetProject = TargetProjectFactory.create(rootPath);
    final GeneratedSourceCode generatedSourceCode = TestUtil.createGeneratedSourceCode(
        targetProject);
    final Configuration config = new Configuration.Builder(targetProject)
        .build();
    final TestExecutor executor = new TestExecutor(config);
    return executor.exec(generatedSourceCode);
  }

  /**
   * ビルドに失敗したプロジェクトのテスト結果を正しくシリアライズできるかテスト
   */
  @Test
  public void testSerializedTestResultsOnBuildFail() {
    // テストの実行
    final TestResults result = execTest("example/BuildFailure01");

    // シリアライズ
    final JsonObject serializedTestResults = gson.toJsonTree(result)
        .getAsJsonObject();
    final double successRate = serializedTestResults.get("successRate")
        .getAsDouble();
    final int executedTestsCount = serializedTestResults.get("executedTestsCount")
        .getAsInt();
    final JsonArray serializedTestResultList = serializedTestResults.get("testResults")
        .getAsJsonArray();

    // 他のキーを持っていないかチェック
    final Set<String> actualKeySet = serializedTestResults.keySet();
    assertThat(actualKeySet).containsExactly(JsonKeyAlias.TestResults.SUCCESS_RATE,
        JsonKeyAlias.TestResults.EXECUTED_TESTS_COUNT,
        JsonKeyAlias.TestResults.TEST_RESULTS
    );

    // 値が正しいかチェック
    assertThat(successRate).isEqualTo(-1.0d);
    assertThat(executedTestsCount).isEqualTo(0);
    assertThat(serializedTestResultList).isEmpty();
  }

  /**
   * ビルドに成功したプロジェクトのテスト結果を正しくシリアライズできるかテスト
   */
  @Test
  public void testSerializedTestResultsOnBuildSuccess() {
    // テストの実行
    final TestResults result = execTest("example/CloseToZero01");

    // シリアライズ
    final JsonObject serializedTestResults = gson.toJsonTree(result)
        .getAsJsonObject();
    final double successRate = serializedTestResults.get("successRate")
        .getAsDouble();
    final int executedTestsCount = serializedTestResults.get("executedTestsCount")
        .getAsInt();
    final JsonArray serializedTestResultList = serializedTestResults.get("testResults")
        .getAsJsonArray();

    // 他のキーを持っていないかチェック
    final Set<String> actualKeySet = serializedTestResults.keySet();
    assertThat(actualKeySet).containsExactly(JsonKeyAlias.TestResults.SUCCESS_RATE,
        JsonKeyAlias.TestResults.EXECUTED_TESTS_COUNT,
        JsonKeyAlias.TestResults.TEST_RESULTS
    );

    // 値が正しいかチェック
    assertThat(successRate).isEqualTo(0.75d);
    assertThat(executedTestsCount).isEqualTo(4);
    assertThat(serializedTestResultList).hasSize(4);

    // 事前に実行したテストのFQNを取得しておく
    final Set<FullyQualifiedName> testFQNs = result.getExecutedTestFQNs();

    // 各テストの実行結果をチェック
    for (final JsonElement element : serializedTestResultList) {
      final JsonObject serializedTestResult = element.getAsJsonObject();
      final String fqnValue = serializedTestResult.get(JsonKeyAlias.TestResult.FQN)
          .getAsString();
      final boolean isSuccess = serializedTestResult.get(JsonKeyAlias.TestResult.IS_SUCCESS)
          .getAsBoolean();
      final FullyQualifiedName fqn = new TestFullyQualifiedName(fqnValue);

      assertThat(testFQNs).contains(fqn);
      final TestResult testResult = result.getTestResult(fqn);
      assertThat(isSuccess).isEqualTo(!testResult.failed);
    }
  }
}
