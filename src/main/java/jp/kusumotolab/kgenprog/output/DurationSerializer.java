package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.time.Duration;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DurationSerializer implements JsonSerializer<Duration> {
  @Override
  public JsonElement serialize(
      final Duration duration, final Type typeOfSrc, final JsonSerializationContext context) {
    return new JsonPrimitive(duration.toString());
  }
}
