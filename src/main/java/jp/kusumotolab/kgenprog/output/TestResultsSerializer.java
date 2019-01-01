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

public class TestResultsSerializer implements JsonSerializer<TestResults> {

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
