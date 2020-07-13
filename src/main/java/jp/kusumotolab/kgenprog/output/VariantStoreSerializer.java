package jp.kusumotolab.kgenprog.output;


import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

/**
 * VariantStoreをシリアライズするクラス.<br>
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
 * <td>projectName</td>
 * <td>プロジェクト名</td>
 * </tr>
 *
 * <tr>
 * <td>variants</td>
 * <td>生成したバリアントの配列</td>
 * </tr>
 *
 * <tr>
 * <td>configuration</td>
 * <td>kGenProgの実行時の設定</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 * @see VariantSerializer
 * @see Configuration
 */
public class VariantStoreSerializer implements JsonSerializer<VariantStore> {

  /**
   * シリアライズを行う.<br>
   *
   * @param variantStore シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */

  @Override
  public JsonElement serialize(final VariantStore variantStore, final Type type,
      final JsonSerializationContext context) {

    final JsonObject serializedVariantStore = new JsonObject();
    final TargetProject targetProject = variantStore.getConfiguration()
        .getTargetProject();
    final String projectName = (targetProject != null) ? targetProject.rootPath.getFileName()
        .toString() : "";

    serializedVariantStore.addProperty("projectName", projectName);
    serializedVariantStore.add("variants", context.serialize(variantStore.getAllVariants()));
    serializedVariantStore.add("configuration", context.serialize(variantStore.getConfiguration()));

    return serializedVariantStore;
  }
}
