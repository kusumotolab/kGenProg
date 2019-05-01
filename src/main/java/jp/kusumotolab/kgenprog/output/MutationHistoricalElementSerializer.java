package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;

/**
 * MutationHistoricalElementをシリアライズするクラス<br>
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
 *
 * <tr>
 * <td>appendBase</td>
 * <td>追加されたBase</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 * @see BaseSerializer
 */
public class MutationHistoricalElementSerializer implements
    JsonSerializer<MutationHistoricalElement> {

  @Override
  public JsonElement serialize(final MutationHistoricalElement mutationHistoricalElement,
      final Type type, final JsonSerializationContext context) {

    // 親のIDと操作の名前をシリアライズする
    final JsonObject serializedMutationHistoricalElement = context.serialize(
        mutationHistoricalElement, HistoricalElement.class)
        .getAsJsonObject();

    serializedMutationHistoricalElement.add("appendBase",
        context.serialize(mutationHistoricalElement.getAppendedBase()));

    return serializedMutationHistoricalElement;
  }
}
