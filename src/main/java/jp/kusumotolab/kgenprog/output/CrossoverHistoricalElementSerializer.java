package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;


/**
 * CrossoverHistoricalElementをシリアライズするクラス</br>
 *
 * 使い方
 * <pre>
 *  {@code
 *    final Gson gson = new GsonBuilder()
 *        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class, new CrossoverHistoricalElementSerializer())
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
 *      "name" : "crossover",
 *      "crossoverPoint" : 5
 *    }
 *  }
 * </pre>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class CrossoverHistoricalElementSerializer implements
    JsonSerializer<CrossoverHistoricalElement> {

  @Override
  public JsonElement serialize(final CrossoverHistoricalElement crossoverHistoricalElement,
      final Type type, final JsonSerializationContext context) {

    // 親のIDと操作の名前をシリアライズする
    final JsonObject serializedCrossoverHistoricalElement = context.serialize(
        crossoverHistoricalElement, HistoricalElement.class)
        .getAsJsonObject();

    serializedCrossoverHistoricalElement.addProperty("crossoverPoint",
        crossoverHistoricalElement.getCrossoverPoint());

    return serializedCrossoverHistoricalElement;
  }
}
