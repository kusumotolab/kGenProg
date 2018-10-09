package jp.kusumotolab.kgenprog.ga;

import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
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
    // Diffの集合を取得する
    final JsonArray serializedDiff = new JsonArray();
    patchGenerator.exec(variant)
        .stream()
        .map(Patch::getDiff)
        .forEach(serializedDiff::add);

    final JsonObject serializedVariant = new JsonObject();

    serializedVariant.addProperty("id", id);
    serializedVariant.addProperty("generation_number", generationNumber);
    serializedVariant.addProperty("fitness", fitness);
    serializedVariant.addProperty("build_success", buildSuccess);
    serializedVariant.add("diff", serializedDiff);
    serializedVariant.add("parents", serializeParents(variant, variant.getHistoricalElement()));

    return serializedVariant;
  }

  private JsonArray serializeParents(final Variant variant,
      final HistoricalElement historicalElement) {

    final JsonArray serializeParents = new JsonArray();
    final List<Variant> parents = historicalElement.getParents();
    final String operationName = historicalElement.getOperationName();
    for (final Variant parent : parents) {
      final JsonArray serializedDiff = new JsonArray();
      patchGenerator.exec(parent, variant)
          .stream()
          .map(Patch::getDiff)
          .forEach(serializedDiff::add);
      final long id = Integer.toUnsignedLong(parent.hashCode());

      final JsonObject serializedParent = new JsonObject();
      serializedParent.addProperty("id", id);
      serializedParent.add("diff", serializedDiff);
      serializedParent.addProperty("operation_name", operationName);

      serializeParents.add(serializedParent);
    }
    return serializeParents;
  }
}
