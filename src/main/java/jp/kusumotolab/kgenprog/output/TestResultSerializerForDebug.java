package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.test.Coverage;
import jp.kusumotolab.kgenprog.project.test.TestResult;

/**
 * デバック用のシリアライザ
 * Mapをjson配列にシリアライズできないのでカスタムシリアライザを使用
 */
public class TestResultSerializerForDebug implements JsonSerializer<TestResult> {

  @Override
  public JsonElement serialize(final TestResult testResult, final Type type,
      final JsonSerializationContext context) {
    final List<Coverage> coverages = testResult.getExecutedTargetFQNs()
        .stream()
        .map(testResult::getCoverages)
        .collect(Collectors.toList());
    final JsonObject serializedTestResults = new JsonObject();
    serializedTestResults.add("executedTestFQN", context.serialize(testResult.executedTestFQN));
    serializedTestResults.addProperty("wasFailed", testResult.failed);
    serializedTestResults.add("coverages", context.serialize(coverages));

    return serializedTestResults;
  }
}
