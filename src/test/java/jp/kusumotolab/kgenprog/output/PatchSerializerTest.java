package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class PatchSerializerTest {

  private Gson gson;
  private final JDTASTConstruction astConstruction = new JDTASTConstruction();
  private final PatchGenerator patchGenerator = new PatchGenerator();

  @Before
  public void setup() {
    gson = new GsonBuilder().registerTypeAdapter(Patch.class, new PatchSerializer())
        .registerTypeHierarchyAdapter(FileDiff.class, new FileDiffSerializer())
        .create();
  }

  private Variant createVariant(final Fitness fitness, final TargetProject targetProject) {

    return new Variant(0, 0, new Gene(Collections.emptyList()),
        astConstruction.constructAST(targetProject), EmptyTestResults.instance, fitness,
        Collections.emptyList(), new OriginalHistoricalElement());
  }

  private Variant createVariant(final long id, final int generationNumber, final Fitness fitness,
      final GeneratedSourceCode code, final HistoricalElement historicalElement) {
    return new Variant(id, generationNumber, new Gene(Collections.emptyList()), code,
        EmptyTestResults.instance, fitness, Collections.emptyList(), historicalElement);
  }

  /**
   * パッチが下の形式でシリアライズされるかテストする
   *
   * [{ "fileName":"ファイル名(fqn)", "diff":"元のプログラムとの差分" }]
   */
  @Test
  public void testPatch() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分を作るために適当な位置にコードを挿入する
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(rootPath, Paths.get("src/example/CloseToZero.java")), statement, ast);

    // 挿入操作を適用する
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("json"));
    final Statement insertStatement = jdtAST.newExpressionStatement(invocation);
    final InsertOperation operation = new InsertOperation(insertStatement);
    final Base appendBase = new Base(location, operation);
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final HistoricalElement historicalElement =
        new MutationHistoricalElement(initialVariant, appendBase);
    final Variant modifiedVariant =
        createVariant(1L, 1, new SimpleFitness(0.0d), code, historicalElement);

    final Patch patch = patchGenerator.exec(modifiedVariant);

    final JsonArray serializedPatches = gson.toJsonTree(patch)
        .getAsJsonArray();
    assertThat(serializedPatches).hasSize(1);

    // FileDiffをシリアライズできているかテスト
    final JsonObject serializedPatch = serializedPatches.get(0)
        .getAsJsonObject();
    final Set<String> serializedPatchKey = serializedPatch.keySet();

    assertThat(serializedPatchKey).containsOnly(JsonKeyAlias.Patch.DIFF,
        JsonKeyAlias.Patch.FILE_NAME);

    final String fileName = serializedPatch.get(JsonKeyAlias.Patch.FILE_NAME)
        .getAsString();
    assertThat(fileName).isEqualTo("example.CloseToZero");

    // パッチ自体はPatchGeneratorTestでテスト済みなので，値が存在するかどうかだけ調べる
    final String diff = serializedPatch.get(JsonKeyAlias.Patch.DIFF)
        .getAsString();
    assertThat(diff).isNotBlank();
  }
}
