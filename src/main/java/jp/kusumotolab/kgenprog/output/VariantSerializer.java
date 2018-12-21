package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class VariantSerializer implements JsonSerializer<Variant> {

  private final PatchGenerator patchGenerator = new PatchGenerator();

  @Override
  public JsonElement serialize(final Variant variant, final Type type,
      final JsonSerializationContext context) {

    final String id = String.valueOf(variant.getId());
    final int generationNumber = variant.getGenerationNumber()
        .get();
    final double fitness = variant.getFitness()
        .getValue();
    final boolean buildSuccess = variant.isBuildSucceeded();
    final Patch patch = patchGenerator.exec(variant);

    final JsonObject serializedVariant = new JsonObject();

    serializedVariant.addProperty("id", id);
    serializedVariant.addProperty("generationNumber", generationNumber);
    serializedVariant.addProperty("selectionCount", variant.getSelectionCount());
    serializedVariant.addProperty("fitness", !Double.isNaN(fitness) ? fitness : -1.0d);
    serializedVariant.addProperty("isBuildSuccess", buildSuccess);
    serializedVariant.add("patch", context.serialize(patch));
    serializedVariant.add("operations", context.serialize(variant.getHistoricalElement()));
    serializedVariant.add("testSummary", context.serialize(variant.getTestResults()));

    return serializedVariant;
  }
}
