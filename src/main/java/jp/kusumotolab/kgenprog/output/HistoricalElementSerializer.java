package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class HistoricalElementSerializer implements JsonSerializer<HistoricalElement> {

  @Override
  public JsonElement serialize(final HistoricalElement historicalElement, final Type type,
      final JsonSerializationContext context) {

    final long[] parentIds = historicalElement.getParents()
        .stream()
        .mapToLong(Variant::getId)
        .toArray();

    final JsonObject serializedHistoricalElement = new JsonObject();
    serializedHistoricalElement.add("parentIds", context.serialize(parentIds));
    serializedHistoricalElement.addProperty("name", historicalElement.getOperationName());

    return serializedHistoricalElement;
  }
}
