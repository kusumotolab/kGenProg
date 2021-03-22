package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.Gene;

/**
 * Geneのシリアライザ．
 * baseの配列を返す
 */
public class GeneSerializer implements JsonSerializer<Gene> {

  /**
   * シリアライズを行う.<br>
   *
   * @param gene シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final Gene gene, final Type type,
      final JsonSerializationContext context) {
    return context.serialize(gene.getBases());
  }
}
