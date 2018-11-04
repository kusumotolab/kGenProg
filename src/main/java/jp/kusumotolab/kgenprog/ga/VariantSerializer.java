package jp.kusumotolab.kgenprog.ga;

import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.Patch;
import jp.kusumotolab.kgenprog.project.PatchGenerator;

public class VariantSerializer implements JsonSerializer<Variant> {

  private final PatchGenerator patchGenerator = new PatchGenerator();

  @Override
  public JsonElement serialize(final Variant variant, final Type type,
      final JsonSerializationContext context) {

    final long id = Integer.toUnsignedLong(variant.hashCode());
    final int generationNumber = variant.getGenerationNumber()
        .get();
    final double rawFitness = variant.getFitness()
        .getValue();
    final boolean buildSuccess = !Double.isNaN(rawFitness);
    final double fitness = buildSuccess ? rawFitness : -1.0d;
    // Pathをシリアライズする
    final List<Patch> patches = patchGenerator.exec(variant);
    final JsonArray serializedPatches = serializePatches(patches);

    final JsonObject serializedVariant = new JsonObject();

    serializedVariant.addProperty("id", id);
    serializedVariant.addProperty("generationNumber", generationNumber);
    serializedVariant.addProperty("fitness", fitness);
    serializedVariant.addProperty("buildSuccess", buildSuccess);
    serializedVariant.add("patches", serializedPatches);
    serializedVariant.add("operations",
        serializeOperations(variant.getHistoricalElement()));

    return serializedVariant;
  }

  private JsonArray serializeOperations(final HistoricalElement historicalElement) {

    final JsonArray serializedOperations = new JsonArray();
    final List<Variant> parents = historicalElement.getParents();
    final String operationName = historicalElement.getOperationName();
    for (final Variant parent : parents) {
      final long id = Integer.toUnsignedLong(parent.hashCode());
      final JsonObject serializedOperation = new JsonObject();

      serializedOperation.addProperty("id", id);
      serializedOperation.addProperty("operationName", operationName);
      serializedOperations.add(serializedOperation);
    }
    return serializedOperations;
  }

  private JsonArray serializePatches(final List<Patch> patches) {
    final JsonArray serializedPatches = new JsonArray();

    for (final Patch patch : patches) {
      final String fileName = patch.fileName;
      final String diff = patch.getDiff();

      final JsonObject serializedPatch = new JsonObject();
      serializedPatch.addProperty("fileName", fileName);
      serializedPatch.addProperty("diff", diff);

      serializedPatches.add(serializedPatch);
    }

    return serializedPatches;
  }
}
