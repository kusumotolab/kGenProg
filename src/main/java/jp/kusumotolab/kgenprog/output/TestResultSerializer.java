package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.test.TestResult;

public class TestResultSerializer implements JsonSerializer<TestResult> {

  @Override
  public JsonElement serialize(final TestResult testResult, final Type type,
      final JsonSerializationContext context) {
    final JsonObject serializedTestResult = new JsonObject();

    serializedTestResult.addProperty("fqn", testResult.executedTestFQN.toString());
    serializedTestResult.addProperty("isSuccess", !testResult.failed);

    return serializedTestResult;
  }
}
