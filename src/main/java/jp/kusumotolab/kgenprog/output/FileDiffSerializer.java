package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * FileDiffをシリアライズするクラス</br>
 *
 * 使い方
 * <pre>
 *  {@code
 *    final Gson gson = new GsonBuilder()
 *        .registerTypeHierarchyAdapter(FileDiff.class, new FileDiffSerializer())
 *        .create();
 *    gson.toJson(base);
 *  }
 * </pre>
 *
 * 出力されるJSON
 * <pre>
 *  {@code
 *    {
 *      "fileName" : "foo.java",
 *      "diff" : "@@ -1,3 +1,3 @@\npublic void bar(){\n-    return;\n+}"
 *    }
 *  }
 * </pre>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
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
