package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * FQNのシリアライズを行うクラス
 *
 * 文字列表現を返す
 */
public class FullyQualifiedNameSerializer implements JsonSerializer<FullyQualifiedName> {

  /**
   * シリアライズを行う.<br>
   *
   * @param fqn シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final FullyQualifiedName fqn, final Type type,
      final JsonSerializationContext context) {
    return new JsonPrimitive(fqn.toString());
  }
}
