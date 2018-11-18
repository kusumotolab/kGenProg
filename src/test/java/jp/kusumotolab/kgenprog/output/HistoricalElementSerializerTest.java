package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;

public class HistoricalElementSerializerTest {

  private Gson gson;
  private JDTASTConstruction astConstruction = new JDTASTConstruction();

  @Before
  public void setup() {
    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .create();
  }

  private Variant createVariant(final Fitness fitness,
      final TargetProject targetProject) {

    return new Variant(0, 0, new Gene(Collections.emptyList()),
        astConstruction.constructAST(targetProject),
        EmptyTestResults.instance, fitness, Collections.emptyList(),
        new OriginalHistoricalElement());
  }

  private Variant createVariant(final long id, final int generationNumber, final Fitness fitness,
      final GeneratedSourceCode code, final HistoricalElement historicalElement) {
    return new Variant(id, generationNumber, new Gene(Collections.emptyList()), code,
        EmptyTestResults.instance, fitness, Collections.emptyList(), historicalElement);
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
    final HistoricalElement historicalElement = new MutationHistoricalElement(initialVariant,
        appendBase);

    final JsonArray serializedHistoricalElement = gson.toJsonTree(historicalElement)
        .getAsJsonArray();

    // 大きさのチェック
    assertThat(serializedHistoricalElement).hasSize(1);

    // キーのチェック
    final JsonObject serializedOperation = serializedHistoricalElement.get(0)
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
    final HistoricalElement historicalElement = new MutationHistoricalElement(initialVariant,
        appendBase);

    final JsonArray serializedHistoricalElement = gson.toJsonTree(historicalElement)
        .getAsJsonArray();

    // 大きさのチェック
    assertThat(serializedHistoricalElement).hasSize(1);

    // キーのチェック
    final JsonObject serializedOperation = serializedHistoricalElement.get(0)
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
    final HistoricalElement historicalElement = new MutationHistoricalElement(initialVariant,
        appendBase);

    final JsonArray serializedOperations = gson.toJsonTree(historicalElement)
        .getAsJsonArray();

    // 大きさのチェック
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
        new GenerationFailedSourceCode(""),
        new MutationHistoricalElement(initialVariant, new Base(null, new InsertOperation(null))));

    // 親2
    final Variant parentB = createVariant(2L, 1, new SimpleFitness(0.0d),
        new GenerationFailedSourceCode(""),
        new MutationHistoricalElement(initialVariant, new Base(null, new InsertOperation(null))));

    // 子供
    final HistoricalElement historicalElement = new CrossoverHistoricalElement(parentA, parentB, 1);

    final JsonArray serializedHistoricalElement = gson.toJsonTree(historicalElement)
        .getAsJsonArray();

    assertThat(serializedHistoricalElement).hasSize(2);

    final Set<String> parentIds = new HashSet<>();
    for (final JsonElement element : serializedHistoricalElement) {
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
