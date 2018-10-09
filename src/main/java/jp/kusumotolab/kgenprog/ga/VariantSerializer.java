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
    final double fitness = !Double.isNaN(rawFitness) ? rawFitness : -1.0d;
    // Pathをシリアライズする
    final List<Patch> patches = patchGenerator.exec(variant);
    final JsonArray serializedPatches = serializePatches(patches);

    final JsonObject serializedVariant = new JsonObject();

    serializedVariant.addProperty("id", id);
    serializedVariant.addProperty("generation_number", generationNumber);
    serializedVariant.addProperty("fitness", fitness);
    serializedVariant.addProperty("build_success", buildSuccess);
    serializedVariant.add("patches", serializedPatches);
    serializedVariant.add("parents", serializeParents(variant, variant.getHistoricalElement()));

    return serializedVariant;
  }

  private JsonArray serializeParents(final Variant variant,
      final HistoricalElement historicalElement) {

    final JsonArray serializeParents = new JsonArray();
    final List<Variant> parents = historicalElement.getParents();
    final String operationName = historicalElement.getOperationName();
    for (final Variant parent : parents) {
      final long id = Integer.toUnsignedLong(parent.hashCode());
      // Pathをシリアライズする
      final List<Patch> patches = patchGenerator.exec(variant);
      final JsonArray serializedPatches = serializePatches(patches);

      final JsonObject serializedParent = new JsonObject();

      serializedParent.addProperty("id", id);
      serializedParent.add("patches", serializedPatches);
      serializedParent.addProperty("operation_name", operationName);

      serializeParents.add(serializedParent);
    }
    return serializeParents;
  }

  private JsonArray serializePatches(final List<Patch> patches) {
    final JsonArray serializedPatches = new JsonArray();

    for (final Patch patch : patches) {
      final String fileName = patch.fileName;
      final String diff = patch.getDiff();

      final JsonObject serializedPatch = new JsonObject();
      serializedPatch.addProperty("file_name", fileName);
      serializedPatch.addProperty("diff", diff);

      serializedPatches.add(serializedPatch);
    }

    return serializedPatches;
  }
}
