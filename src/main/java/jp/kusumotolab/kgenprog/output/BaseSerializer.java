package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.project.LineNumberRange;

public class BaseSerializer implements JsonSerializer<Base> {

  @Override
  public JsonElement serialize(final Base base, final Type type,
      final JsonSerializationContext context) {

    final JsonObject serializedBase = new JsonObject();

    serializedBase.addProperty("name", base.getOperation()
        .getName());
    serializedBase.addProperty("fileName", base.getTargetLocation()
        .getSourcePath().path.toString());
    serializedBase.addProperty("snippet", base.getOperation()
        .getTargetSnippet());
    serializedBase.add("lineNumberRange", context.serialize(base.getTargetLocation()
            .inferLineNumbers(),
        LineNumberRange.class));

    return serializedBase;
  }
}
