package jp.kusumotolab.kgenprog.ga;


import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class VariantStoreSerializer implements JsonSerializer<VariantStore> {

  @Override
  public JsonElement serialize(final VariantStore variantStore, final Type type,
      final JsonSerializationContext context) {

    final JsonObject serializedVariantStore = new JsonObject();
    final TargetProject targetProject = variantStore.getTargetProject();
    final String projectName = (targetProject != null) ? targetProject.rootPath.getFileName()
        .toString() : "";
    final JsonElement serializedVariants = context.serialize(variantStore.getAllVariants());

    serializedVariantStore.addProperty("projectName", projectName);
    serializedVariantStore.add("variants", serializedVariants);

    return serializedVariantStore;
  }
}
