package jp.kusumotolab.kgenprog.output;


import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.VariantStore;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class VariantStoreSerializer implements JsonSerializer<VariantStore> {

  private final Configuration config;

  public VariantStoreSerializer(final Configuration config) {
    this.config = config;
  }

  @Override
  public JsonElement serialize(final VariantStore variantStore, final Type type,
      final JsonSerializationContext context) {

    final JsonObject serializedVariantStore = new JsonObject();
    final TargetProject targetProject = config.getTargetProject();
    final String projectName = (targetProject != null) ? targetProject.rootPath.getFileName()
        .toString() : "";

    serializedVariantStore.addProperty("projectName", projectName);
    serializedVariantStore.add("variants", context.serialize(variantStore.getAllVariants()));

    return serializedVariantStore;
  }
}
