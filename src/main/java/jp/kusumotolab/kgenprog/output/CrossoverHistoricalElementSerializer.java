package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;

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
