package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;

/**
 * fitnessのシリアライザ．
 */
public class FitnessSerializer implements JsonSerializer<Fitness> {

  /**
   * シリアライズを行う.<br>
   *
   * @param fitness シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final Fitness fitness, final Type type,
      final JsonSerializationContext context) {
    final double normalizedValue = fitness.getNormalizedValue();
    // NaN対策
    return new JsonPrimitive(!Double.isNaN(normalizedValue) ? normalizedValue : -1.0d);
  }
}
