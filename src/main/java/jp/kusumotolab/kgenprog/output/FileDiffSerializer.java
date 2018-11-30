package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class FileDiffSerializer implements JsonSerializer<FileDiff> {

  @Override
  public JsonElement serialize(final FileDiff fileDiff, final Type type,
      final JsonSerializationContext context) {

    final JsonObject serializedFileDiff = new JsonObject();
    serializedFileDiff.addProperty("fileName", fileDiff.fileName);
    serializedFileDiff.addProperty("diff", fileDiff.getDiff());

    return serializedFileDiff;
  }
}
