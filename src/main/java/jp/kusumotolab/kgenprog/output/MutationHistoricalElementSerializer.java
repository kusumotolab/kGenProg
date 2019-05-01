package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;

/**
 * MutationHistoricalElementをシリアライズするクラス</br>
 *
 * 使い方
 * <pre>
 *  {@code
 *    final Gson gson = new GsonBuilder()
 *        .registerTypeHierarchyAdapter(MutationHistoricalElement.class, new MutationHistoricalElementSerializer())
 *        .create();
 *    gson.toJson(base);
 *  }
 * </pre>
 *
 * 出力されるJSON
 * <pre>
 *  {@code
 *    {
 *      "parentIds": [1, 3],
 *      "name" : "insert",
 *      "appendBase" : {
 *        {
 *         "name": "insert",
 *         "fileName" : "foo.java",
 *         "snippet" : "return n--;",
 *         "lineNumberRange" : [10, 12]
 *        }
 *      }
 *    }
 *  }
 * </pre>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class MutationHistoricalElementSerializer implements
    JsonSerializer<MutationHistoricalElement> {

  @Override
  public JsonElement serialize(final MutationHistoricalElement mutationHistoricalElement,
      final Type type, final JsonSerializationContext context) {

    // 親のIDと操作の名前をシリアライズする
    final JsonObject serializedMutationHistoricalElement = context.serialize(
        mutationHistoricalElement, HistoricalElement.class)
        .getAsJsonObject();

    serializedMutationHistoricalElement.add("appendBase",
        context.serialize(mutationHistoricalElement.getAppendedBase()));

    return serializedMutationHistoricalElement;
  }
}
