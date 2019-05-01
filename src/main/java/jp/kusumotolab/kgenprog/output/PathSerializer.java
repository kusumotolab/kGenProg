package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.nio.file.Path;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
/**
 * Pathをシリアライズするクラス</br>
 * JSONオブジェクトではなく文字列型を返す．
 *
 * 使い方
 * <pre>
 *  {@code
 *    final Gson gson = new GsonBuilder()
 *        .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
 *        .create();
 *    gson.toJson(base);
 *  }
 * </pre>
 *
 * 出力されるJSON
 * <pre>
 *  {@code
 *    "foo.java"
 *  }
 * </pre>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class PathSerializer implements JsonSerializer<Path> {

  @Override
  public JsonElement serialize(final Path src, final Type type,
      final JsonSerializationContext context) {
    return new JsonPrimitive(src.toString());
  }
}