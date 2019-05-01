package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * Variantをシリアライズするクラス</br>
 *
 * 使い方
 * <pre>
 *  {@code
 *    final Gson gson = new GsonBuilder()
 *        .registerTypeHierarchyAdapter(Variant.class, new VariantSerializer())
 *        .create();
 *    gson.toJson(base);
 *  }
 * </pre>
 *
 * 出力されるJSON
 * <pre>
 *  {@code
 *    {
 *      "id" : 1,
 *      "generationNumber" : 1,
 *      "selectionCount" : 1,
 *      "fitness" : "0.0",
 *      "isBuildSuccess" : true,
 *      "isSyntaxValid" : true,
 *      "bases" : [],
 *      "patch" : "",
 *      "operation" : {"parentIds":[], "name":""},
 *      "testSummary" : {}
 *    }
 *  }
 * </pre>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class VariantSerializer implements JsonSerializer<Variant> {

  private final PatchGenerator patchGenerator = new PatchGenerator();

  @Override
  public JsonElement serialize(final Variant variant, final Type type,
      final JsonSerializationContext context) {

    final int generationNumber = variant.getGenerationNumber()
        .get();
    final double fitness = variant.getFitness()
        .getValue();
    final Patch patch = patchGenerator.exec(variant);

    final JsonObject serializedVariant = new JsonObject();

    serializedVariant.addProperty("id", variant.getId());
    serializedVariant.addProperty("generationNumber", generationNumber);
    serializedVariant.addProperty("selectionCount", variant.getSelectionCount());
    serializedVariant.addProperty("fitness", !Double.isNaN(fitness) ? fitness : -1.0d);
    serializedVariant.addProperty("isBuildSuccess", variant.isBuildSucceeded());
    serializedVariant.addProperty("isSyntaxValid", variant.isSyntaxValid());
    serializedVariant.add("bases", context.serialize(variant.getGene()
        .getBases()));
    serializedVariant.add("patch", context.serialize(patch));
    serializedVariant.add("operation", context.serialize(variant.getHistoricalElement()));
    serializedVariant.add("testSummary", context.serialize(variant.getTestResults()));

    return serializedVariant;
  }
}
