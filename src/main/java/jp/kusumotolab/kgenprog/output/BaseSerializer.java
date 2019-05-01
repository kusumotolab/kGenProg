package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.project.LineNumberRange;

/**
 * Baseをシリアライズするクラス</br>
 *
 * 使い方
 * <pre>
 *  {@code
 *    final Gson gson = new GsonBuilder()
 *        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
 *        .create();
 *    gson.toJson(base);
 *  }
 * </pre>
 *
 * 出力されるJSON
 * <pre>
 *  {@code
 *    {
 *      "name": "insert",
 *      "fileName" : "foo.java",
 *      "snippet" : "return n--;",
 *      "lineNumberRange" : [10, 12]
 *    }
 *  }
 * </pre>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class BaseSerializer implements JsonSerializer<Base> {

  /**
   * シリアライズを行う</br>
   *
   * @param base シリアライズ対象のオブジェクト
   * @param type シリアライズ対象のオブジェクトの型
   * @param context オブジェクトをシリアライズするクラス
   */
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
