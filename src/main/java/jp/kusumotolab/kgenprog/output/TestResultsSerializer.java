package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * TestResultsをシリアライズするクラス<br>
 *
 * <table border="1">
 * <thead>
 * <tr>
 * <td>キー</td>
 * <td>説明</td>
 * </tr>
 * </thead>
 *
 * <tbody>
 * <tr>
 * <td>successRate</td>
 * <td>テストの通過率</td>
 * </tr>
 *
 * <tr>
 * <td>executedTestsCount</td>
 * <td>実行したテストの数</td>
 * </tr>
 *
 * <tr>
 * <td>testResults</td>
 * <td>シリアラズされた{@link TestResult}の配列</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 * @see TestResultSerializer
 */
public class TestResultsSerializer implements JsonSerializer<TestResults> {

  /**
   * シリアライズを行う<br>
   *
   * @param testResults シリアライズ対象のオブジェクト
   * @param type シリアライズ対象のオブジェクトの型
   * @param context シリアライズ対象以外のオブジェクトをシリアライズするときに使うオブジェクト
   */
  @Override
  public JsonElement serialize(final TestResults testResults, final Type type,
      final JsonSerializationContext context) {

    final JsonObject serializedTestSummary = new JsonObject();
    final double successRate = testResults.getSuccessRate();
    // TestResultの集合を取得する
    // fqn の辞書順にソートする
    final List<TestResult> testResultList = testResults.getExecutedTestFQNs()
        .stream()
        .map(testResults::getTestResult)
        .sorted(Comparator.comparing(e -> e.executedTestFQN.value))
        .collect(Collectors.toList());
    final JsonElement serializedTestResultList = context.serialize(testResultList);

    serializedTestSummary.addProperty("successRate",
        !Double.isNaN(successRate) ? successRate : -1.0d);
    serializedTestSummary.addProperty("executedTestsCount", testResults.getExecutedTestFQNs()
        .size());
    serializedTestSummary.add("testResults", serializedTestResultList);

    return serializedTestSummary;
  }
}
