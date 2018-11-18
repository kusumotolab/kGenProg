package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PatchSerializer implements JsonSerializer<Patch> {

  @Override
  public JsonElement serialize(final Patch patch, final Type type,
      final JsonSerializationContext context) {

    return context.serialize(patch.getAll());
  }
}
