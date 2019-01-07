package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;

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
