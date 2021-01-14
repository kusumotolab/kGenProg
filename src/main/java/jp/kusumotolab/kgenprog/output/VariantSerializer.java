package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.nio.file.Path;
import org.eclipse.jdt.core.dom.ASTNode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.gsonfire.GsonFireBuilder;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

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

  private final Gson gson;
  private final PatchGenerator patchGenerator = new PatchGenerator();

  public VariantSerializer() {
    gson = new GsonFireBuilder().enableExposeMethodResult()
        .createGsonBuilder()
        .registerTypeHierarchyAdapter(ASTNode.class, new ASTNodeSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .registerTypeHierarchyAdapter(FileDiff.class, new FileDiffSerializer())
        .registerTypeHierarchyAdapter(Fitness.class, new FitnessSerializer())
        .registerTypeHierarchyAdapter(FullyQualifiedName.class, new FullyQualifiedNameSerializer())
        .registerTypeHierarchyAdapter(Gene.class, new GeneSerializer())
        .registerTypeHierarchyAdapter(GeneratedJDTAST.class, new GeneratedJDTASTSerializer())
        .registerTypeHierarchyAdapter(GeneratedSourceCode.class,
            new GeneratedSourceCodeSerializer())
        .registerTypeHierarchyAdapter(Patch.class, new PatchSerializer())
        .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
        .registerTypeHierarchyAdapter(SourcePath.class, new SourcePathSerializer())
        .registerTypeHierarchyAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class,
            new CrossoverHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(MutationHistoricalElement.class,
            new MutationHistoricalElementSerializer())
        .create();
  }

  @Override
  public JsonElement serialize(final Variant variant, final Type type,
      final JsonSerializationContext context) {
    // カスタムシリアライザを使わずにシリアライズする
    final Patch patch = patchGenerator.exec(variant);
    final JsonObject serializedVariant = gson.toJsonTree(variant)
        .getAsJsonObject();

    // パッチの情報を追加
    serializedVariant.add("patch", context.serialize(patch));

    return serializedVariant;
  }
}
