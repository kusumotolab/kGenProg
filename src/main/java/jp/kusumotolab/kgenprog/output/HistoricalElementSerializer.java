package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * HistoricalElementをシリアライズするクラス.<br>
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
 * <td>parentIds</td>
 * <td>親バリアントのID</td>
 * </tr>
 *
 * <tr>
 * <td>name</td>
 * <td>適用した操作の名前</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class HistoricalElementSerializer implements JsonSerializer<HistoricalElement> {

  /**
   * シリアライズを行う.<br>
   *
   * @param historicalElement シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final HistoricalElement historicalElement, final Type type,
      final JsonSerializationContext context) {

    final long[] parentIds = historicalElement.getParents()
        .stream()
        .mapToLong(Variant::getId)
        .toArray();

    final JsonObject serializedHistoricalElement = new JsonObject();
    serializedHistoricalElement.add("parentIds", context.serialize(parentIds));
    serializedHistoricalElement.addProperty("name", historicalElement.getOperationName());

    return serializedHistoricalElement;
  }
}
