package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonArray;
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

    final JsonArray serializedHistoricalElement = new JsonArray();

    for (final Variant parent : historicalElement.getParents()) {
      final JsonObject serializedOperation = new JsonObject();
      serializedOperation.addProperty("id", String.valueOf(parent.getId()));
      serializedOperation.addProperty("operationName", historicalElement.getOperationName());

      serializedHistoricalElement.add(serializedOperation);
    }

    return serializedHistoricalElement;
  }
}
