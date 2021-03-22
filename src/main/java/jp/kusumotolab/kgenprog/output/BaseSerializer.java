package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * Baseをシリアライズするクラス.<br>
 *
 * <table border="1">
 * <thead>
 * <tr>
 * <td>キー</td>
 * <td>説明</td>
 * </tr>
 * </thead>
 *
 * <tbody>
 * <tr>
 * <td>name</td>
 * <td>適用した操作の名前</td>
 * </tr>
 *
 * <tr>
 * <td>fileName</td>
 * <td>操作の対象となったファイルの名前</td>
 * </tr>
 *
 * <tr>
 * <td>snippet</td>
 * <td>操作によって追加・削除されたコード片</td>
 * </tr>
 *
 * <tr>
 * <td>lineNumberRange</td>
 * <td>操作が適用された行の範囲([始点, 終点])</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class BaseSerializer implements JsonSerializer<Base> {

  /**
   * シリアライズを行う.<br>
   *
   * @param base シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final Base base, final Type type,
      final JsonSerializationContext context) {

    final Operation operation = base.getOperation();
    final ASTLocation targetLocation = base.getTargetLocation();
    final JsonObject serializedBase = new JsonObject();

    serializedBase.addProperty("name", operation.getName());
    serializedBase.add("fileName",
        context.serialize(targetLocation.getSourcePath()));
    serializedBase.addProperty("snippet", operation.getTargetSnippet());
    serializedBase.add("lineNumberRange", context.serialize(targetLocation.inferLineNumbers()));

    return serializedBase;
  }
}
