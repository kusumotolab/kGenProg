package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResultSerializer;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.project.test.TestResultsSerializer;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class VariantSerializerTest {

  private Gson gson;
  private JDTASTConstruction astConstruction = new JDTASTConstruction();

  @Before
  public void setup() {
    gson = new GsonBuilder().registerTypeAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .disableHtmlEscaping()
        .create();
  }

  private Variant createVariant(final Fitness fitness,
      final TargetProject targetProject) {
    return new Variant(0, 0, new Gene(Collections.emptyList()),
        new GeneratedSourceCode(astConstruction.constructAST(targetProject)),
        EmptyTestResults.instance, fitness, Collections.emptyList(),
        new OriginalHistoricalElement());
  }

  private Variant createVariant(final long id, final int generationNumber, final Fitness fitness,
      final GeneratedSourceCode code, final HistoricalElement historicalElement) {
    return new Variant(id, generationNumber, new Gene(Collections.emptyList()), code,
        EmptyTestResults.instance, fitness, Collections.emptyList(), historicalElement);
  }

  /**
   * パッチが下の形式でシリアライズされるかテストする
   *
   * { "fileName":"ファイル名(fqn)", "diff":"元のプログラムとの差分" }
   */
  @Test
  public void testPatches() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分を作るために適当な位置にコードを挿入する
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST ast = (GeneratedJDTAST) originalSourceCode.getAsts()
        .get(0);
    final TypeDeclaration type = (TypeDeclaration) ast.getRoot()
        .types()
        .get(0);
    final MethodDeclaration method = type.getMethods()[0];
    final Statement statement = (Statement) method.getBody()
        .statements()
        .get(0);
    final JDTASTLocation location = new JDTASTLocation(
        new ProductSourcePath(rootPath.resolve("src/example/CloseToZero.java")), statement);

    // 挿入操作を適用する
    final AST jdtAST = ast.getRoot()
        .getAST();
    final MethodInvocation invocation = jdtAST.newMethodInvocation();
    invocation.setName(jdtAST.newSimpleName("json"));
    final Statement insertStatement = jdtAST.newExpressionStatement(invocation);
    final InsertOperation operation = new InsertOperation(insertStatement);
    final Base appendBase = new Base(location, operation);
    final GeneratedSourceCode code = operation.apply(originalSourceCode, location);
    final HistoricalElement historicalElement = new MutationHistoricalElement(initialVariant,
        appendBase);
    final Variant modifiedVariant = createVariant(1L, 1, new SimpleFitness(0.0d), code,
        historicalElement);

    final JsonObject serializedVariant = gson.toJsonTree(modifiedVariant)
        .getAsJsonObject();
    final JsonArray serializedPatches = serializedVariant.get(JsonKeyAlias.Variant.PATCHES)
        .getAsJsonArray();
    assertThat(serializedPatches).hasSize(1);

    // パッチをシリアライズできているかテスト
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

  /**
   * Variantがシリアライズされているかテストする
   *
   * 生成されるパッチのテスト・テスト結果のテストはしない
   */
  @Test
  public void testVariant() {
    // 初期Variantの作成
    final Path rootPath = Paths.get("example/CloseToZero01");
    TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant variant = createVariant(new SimpleFitness(0.0d), project);

    final JsonObject serializedVariant = gson.toJsonTree(variant)
        .getAsJsonObject();

    // キーを持っているかチェック
    final Set<String> serializedVariantKey = serializedVariant.keySet();
    assertThat(serializedVariantKey).containsOnly(JsonKeyAlias.Variant.ID,
        JsonKeyAlias.Variant.FITNESS,
        JsonKeyAlias.Variant.GENERATION_NUMBER,
        JsonKeyAlias.Variant.IS_BUILD_SUCCESS,
        JsonKeyAlias.Variant.OPERATIONS,
        JsonKeyAlias.Variant.TEST_SUMMARY,
        JsonKeyAlias.Variant.SELECTION_COUNT,
        JsonKeyAlias.Variant.PATCHES);

    // 各値のチェック
    final String id = serializedVariant.get(JsonKeyAlias.Variant.ID)
        .getAsString();
    assertThat(id).isEqualTo(String.valueOf(0L));

    final double fitness = serializedVariant.get(JsonKeyAlias.Variant.FITNESS)
        .getAsDouble();
    assertThat(fitness).isEqualTo(0.0d);

    final int generationNumber = serializedVariant.get(JsonKeyAlias.Variant.GENERATION_NUMBER)
        .getAsInt();
    assertThat(generationNumber).isEqualTo(0);

    final boolean isBuildSuccess = serializedVariant.get(JsonKeyAlias.Variant.IS_BUILD_SUCCESS)
        .getAsBoolean();
    assertThat(isBuildSuccess).isEqualTo(true);

    final JsonArray serializedOperations = serializedVariant.get(JsonKeyAlias.Variant.OPERATIONS)
        .getAsJsonArray();
    assertThat(serializedOperations).isEmpty();

    final int selectionCount = serializedVariant.get(JsonKeyAlias.Variant.SELECTION_COUNT)
        .getAsInt();
    assertThat(selectionCount).isEqualTo(0);

    final JsonArray serializedPatches = serializedVariant.get(JsonKeyAlias.Variant.PATCHES)
        .getAsJsonArray();
    assertThat(serializedPatches).isEmpty();
  }

  /**
   * Variantがシリアライズされているかテストする(挿入)
   *
   * 生成されるパッチのテスト・テスト結果のテストはしない
   */
  @Test
  public void testInsertOperation() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分はいらないのでNullやNullObjectにする
    final Operation operation = new InsertOperation(null);
    final Base appendBase = new Base(null, operation);
    final GeneratedSourceCode code = GenerationFailedSourceCode.instance;
    final HistoricalElement historicalElement = new MutationHistoricalElement(initialVariant,
        appendBase);
    final Variant modifiedVariant = createVariant(1L, 1, new SimpleFitness(0.0d), code,
        historicalElement);

    final JsonObject serializedVariant = gson.toJsonTree(modifiedVariant)
        .getAsJsonObject();
    final JsonArray serializedOperations = serializedVariant.get(JsonKeyAlias.Variant.OPERATIONS)
        .getAsJsonArray();

    assertThat(serializedOperations).hasSize(1);

    // キーのチェック
    final JsonObject serializedOperation = serializedOperations.get(0)
        .getAsJsonObject();
    final Set<String> serializedOperationKey = serializedOperation.keySet();

    assertThat(serializedOperationKey).containsOnly(JsonKeyAlias.Operation.ID,
        JsonKeyAlias.Operation.OPERATION_NAME);

    final String parentId = serializedOperation.get(JsonKeyAlias.Operation.ID)
        .getAsString();
    assertThat(parentId).isEqualTo(String.valueOf(0L));

    final String operationName = serializedOperation.get(JsonKeyAlias.Operation.OPERATION_NAME)
        .getAsString();
    assertThat(operationName).isEqualTo("insert");
  }

  /**
   * Variantがシリアライズされているかテストする(削除)
   *
   * 生成されるパッチのテスト・テスト結果のテストはしない
   */
  @Test
  public void testDeleteOperation() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分はいらないのでNullやNullObjectにする
    final Operation operation = new DeleteOperation();
    final Base appendBase = new Base(null, operation);
    final GeneratedSourceCode code = GenerationFailedSourceCode.instance;
    final HistoricalElement historicalElement = new MutationHistoricalElement(initialVariant,
        appendBase);
    final Variant modifiedVariant = createVariant(1L, 1, new SimpleFitness(0.0d), code,
        historicalElement);

    final JsonObject serializedVariant = gson.toJsonTree(modifiedVariant)
        .getAsJsonObject();
    final JsonArray serializedOperations = serializedVariant.get(JsonKeyAlias.Variant.OPERATIONS)
        .getAsJsonArray();

    assertThat(serializedOperations).hasSize(1);

    // キーのチェック
    final JsonObject serializedOperation = serializedOperations.get(0)
        .getAsJsonObject();
    final Set<String> serializedOperationKey = serializedOperation.keySet();

    assertThat(serializedOperationKey).containsOnly(JsonKeyAlias.Operation.ID,
        JsonKeyAlias.Operation.OPERATION_NAME);

    final String parentId = serializedOperation.get(JsonKeyAlias.Operation.ID)
        .getAsString();
    assertThat(parentId).isEqualTo(String.valueOf(0L));

    final String operationName = serializedOperation.get(JsonKeyAlias.Operation.OPERATION_NAME)
        .getAsString();
    assertThat(operationName).isEqualTo("delete");
  }

  /**
   * Variantがシリアライズされているかテストする(置換)
   *
   * 生成されるパッチのテスト・テスト結果のテストはしない
   */
  @Test
  public void testReplace() {

    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分はいらないのでNullやNullObjectにする
    final Operation operation = new ReplaceOperation(null);
    final Base appendBase = new Base(null, operation);
    final GeneratedSourceCode code = GenerationFailedSourceCode.instance;
    final HistoricalElement historicalElement = new MutationHistoricalElement(initialVariant,
        appendBase);
    final Variant modifiedVariant = createVariant(1L, 1, new SimpleFitness(0.0d), code,
        historicalElement);

    final JsonObject serializedVariant = gson.toJsonTree(modifiedVariant)
        .getAsJsonObject();
    final JsonArray serializedOperations = serializedVariant.get(JsonKeyAlias.Variant.OPERATIONS)
        .getAsJsonArray();

    assertThat(serializedOperations).hasSize(1);

    // キーのチェック
    final JsonObject serializedOperation = serializedOperations.get(0)
        .getAsJsonObject();
    final Set<String> serializedOperationKey = serializedOperation.keySet();

    assertThat(serializedOperationKey).containsOnly(JsonKeyAlias.Operation.ID,
        JsonKeyAlias.Operation.OPERATION_NAME);

    final String parentId = serializedOperation.get(JsonKeyAlias.Operation.ID)
        .getAsString();
    assertThat(parentId).isEqualTo(String.valueOf(0L));

    final String operationName = serializedOperation.get(JsonKeyAlias.Operation.OPERATION_NAME)
        .getAsString();
    assertThat(operationName).isEqualTo("replace");
  }

  /**
   * Variantがシリアライズされているかテストする(交叉)
   *
   * 生成されるパッチのテスト・テスト結果のテストはしない
   */
  @Test
  public void testCrossover() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 親1
    final Variant parentA = createVariant(1L, 1, new SimpleFitness(0.0d),
        GenerationFailedSourceCode.instance,
        new MutationHistoricalElement(initialVariant, new Base(null, new InsertOperation(null))));

    // 親2
    final Variant parentB = createVariant(2L, 1, new SimpleFitness(0.0d),
        GenerationFailedSourceCode.instance,
        new MutationHistoricalElement(initialVariant, new Base(null, new InsertOperation(null))));

    // 子供
    final HistoricalElement historicalElement = new CrossoverHistoricalElement(parentA, parentB, 1);
    final Variant child = createVariant(3L, 2, new SimpleFitness(0.0d),
        GenerationFailedSourceCode.instance,
        historicalElement);

    final JsonObject serializedChild = gson.toJsonTree(child)
        .getAsJsonObject();
    final JsonArray serializedOperations = serializedChild.get(JsonKeyAlias.Variant.OPERATIONS)
        .getAsJsonArray();

    assertThat(serializedOperations).hasSize(2);

    final Set<String> parentIds = new HashSet<>();
    for (final JsonElement element : serializedOperations) {
      final JsonObject serializedOperation = element.getAsJsonObject();

      // キーのチェック
      assertThat(serializedOperation.keySet()).containsOnly(JsonKeyAlias.Operation.ID,
          JsonKeyAlias.Operation.OPERATION_NAME);

      final String parentId = serializedOperation.get(JsonKeyAlias.Operation.ID)
          .getAsString();
      parentIds.add(parentId);

      final String operationName = serializedOperation.get(JsonKeyAlias.Operation.OPERATION_NAME)
          .getAsString();
      assertThat(operationName).isEqualTo("crossover");
    }
    // IDのチェック
    assertThat(parentIds).containsOnly(String.valueOf(1L), String.valueOf(2L));
  }
}
