package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * GeneratedSourceCodeのシリアライザ．
 * productAstsだけシリアライズする
 */
public class GeneratedSourceCodeSerializer implements JsonSerializer<GeneratedSourceCode> {

  /**
   * シリアライズを行う.<br>
   *
   * @param generatedSourceCode シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final GeneratedSourceCode generatedSourceCode, final Type type,
      final JsonSerializationContext context) {
    return context.serialize(generatedSourceCode.getProductAsts());
  }
}
