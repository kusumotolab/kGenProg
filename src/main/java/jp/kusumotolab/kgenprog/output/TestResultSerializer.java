package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.test.TestResult;

/**
 * TestResultをシリアライズするクラス</br>
 *
 * 使い方
 * <pre>
 *  {@code
 *    final Gson gson = new GsonBuilder()
 *        .registerTypeHierarchyAdapter(TestResult.class, new TestResultSerializer())
 *        .create();
 *    gson.toJson(base);
 *  }
 * </pre>
 *
 * 出力されるJSON
 * <pre>
 *  {@code
 *    {
 *      "fqn" : "example.FooTest.test01",
 *      "isSuccess" : true
 *    }
 *  }
 * </pre>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
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
