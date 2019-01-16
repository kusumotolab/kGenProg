package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.nio.file.Path;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PathSerializer implements JsonSerializer<Path> {

  @Override
  public JsonElement serialize(final Path src, final Type type,
      final JsonSerializationContext context) {
    return new JsonPrimitive(src.toString());
  }
}