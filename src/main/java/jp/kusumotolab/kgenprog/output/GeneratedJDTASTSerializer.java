package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

/**
 * GeneratedASTのシリアライザ．
 * GeneratedASTのすべてのフィールドをシリアライズするとJSONが巨大になるので実装
 */
public class GeneratedJDTASTSerializer implements JsonSerializer<GeneratedJDTAST<SourcePath>> {

  /**
   * シリアライズを行う.<br>
   *
   * @param generatedJDTAST シリアライズ対象のインスタンス
   * @param type シリアライズ対象のインスタンスの型
   * @param context インスタンスをシリアライズするインスタンス
   */
  @Override
  public JsonElement serialize(final GeneratedJDTAST<SourcePath> generatedJDTAST, final Type type,
      final JsonSerializationContext context) {
    final CompilationUnit root = generatedJDTAST.getRoot();
    final FullyQualifiedName primaryClassName = generatedJDTAST.getPrimaryClassName();

    final JsonObject serializedGeneratedJDTAST = new JsonObject();
    serializedGeneratedJDTAST.add("root", context.serialize(root, ASTNode.class));
    serializedGeneratedJDTAST.add("primaryClassName",
        context.serialize(primaryClassName, FullyQualifiedName.class));

    return new JsonPrimitive(generatedJDTAST.getSourceCode());
  }
}
