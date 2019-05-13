package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;


/**
 * CrossoverHistoricalElementをシリアライズするクラス.<br>
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
 * <td>crossoverPoint</td>
 * <td>交叉点</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class CrossoverHistoricalElementSerializer implements
    JsonSerializer<CrossoverHistoricalElement> {

  /**
   * シリアライズを行う.<br>
   *
   * @param crossoverHistoricalElement シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final CrossoverHistoricalElement crossoverHistoricalElement,
      final Type type, final JsonSerializationContext context) {

    // 親のIDと操作の名前をシリアライズする
    final JsonObject serializedCrossoverHistoricalElement = context.serialize(
        crossoverHistoricalElement, HistoricalElement.class)
        .getAsJsonObject();

    serializedCrossoverHistoricalElement.addProperty("crossoverPoint",
        crossoverHistoricalElement.getCrossoverPoint());

    return serializedCrossoverHistoricalElement;
  }
}
