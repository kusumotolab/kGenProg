package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * Variantをシリアライズするクラス.<br>
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
 * <td>id</td>
 * <td>ID</td>
 * </tr>
 *
 * <tr>
 * <td>generationNumber</td>
 * <td>生成された世代</td>
 * </tr>
 *
 * <tr>
 * <td>selectionCount</td>
 * <td>次世代のバリアントに選ばれた回数</td>
 * </tr>
 *
 * <tr>
 * <td>fitness</td>
 * <td>適応度．Nanのときは-1に変換する．</td>
 * </tr>
 *
 * <tr>
 * <td>isBuildSuccess</td>
 * <td>ビルド結果</td>
 * </tr>
 *
 * <tr>
 * <td>isSyntaxValid</td>
 * <td>文法的に正しいか</td>
 * </tr>
 *
 * <tr>
 * <td>bases</td>
 * <td>塩基の配列</td>
 * </tr>
 *
 * <tr>
 * <td>patch</td>
 * <td>0世代目のバリアントとの差分</td>
 * </tr>
 *
 * <tr>
 * <td>operation</td>
 * <td>適用した操作の配列</td>
 * </tr>
 *
 * <tr>
 * <td>testSummary</td>
 * <td>テスト結果</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 * @see BaseSerializer
 * @see PatchSerializer
 * @see HistoricalElementSerializer
 * @see MutationHistoricalElementSerializer
 * @see TestResultSerializer
 */
public class VariantSerializer implements JsonSerializer<Variant> {

  private final PatchGenerator patchGenerator = new PatchGenerator();

  /**
   * シリアライズを行う.<br>
   *
   * @param variant シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final Variant variant, final Type type,
      final JsonSerializationContext context) {

    final int generationNumber = variant.getGenerationNumber()
        .get();
    final String fitness = variant.getFitness()
        .toString();
    final Patch patch = patchGenerator.exec(variant);

    final JsonObject serializedVariant = new JsonObject();

    serializedVariant.addProperty("id", variant.getId());
    serializedVariant.addProperty("generationNumber", generationNumber);
    serializedVariant.addProperty("selectionCount", variant.getSelectionCount());
    serializedVariant.addProperty("fitness", fitness);
    serializedVariant.addProperty("isBuildSuccess", variant.isBuildSucceeded());
    serializedVariant.addProperty("isSyntaxValid", variant.isSyntaxValid());
    serializedVariant.add("bases", context.serialize(variant.getGene()
        .getBases()));
    serializedVariant.add("patch", context.serialize(patch));
    serializedVariant.add("operation", context.serialize(variant.getHistoricalElement()));
    serializedVariant.add("testSummary", context.serialize(variant.getTestResults()));

    return serializedVariant;
  }
}
