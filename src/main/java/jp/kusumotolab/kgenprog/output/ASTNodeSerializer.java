package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import org.eclipse.jdt.core.dom.ASTNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * ASTのノードのシリアライザ．
 * 循環参照による無限ループを防ぐために実装．
 */
public class ASTNodeSerializer implements JsonSerializer<ASTNode> {

  /**
   * シリアライズを行う.<br>
   *
   * @param node シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final ASTNode node, final Type type,
      final JsonSerializationContext context) {
    return new JsonPrimitive(node.toString());
  }
}
