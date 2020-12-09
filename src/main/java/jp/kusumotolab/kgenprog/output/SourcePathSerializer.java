package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * SourcePathのシリアライザ．
 */
public class SourcePathSerializer implements JsonSerializer<SourcePath> {

  /**
   * シリアライズを行う.<br>
   *
   * @param sourcePath シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final SourcePath sourcePath, final Type type,
      final JsonSerializationContext context) {
    return new JsonPrimitive(sourcePath.toString());
  }
}
