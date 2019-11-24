package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import org.eclipse.jdt.core.dom.ASTNode;
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
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class MutationHistoricalElementSerializerTest {

  private Gson gson;
  private final JDTASTConstruction astConstruction = new JDTASTConstruction();

  @Before
  public void setup() {
    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeHierarchyAdapter(MutationHistoricalElement.class,
            new MutationHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .create();
  }

  private ASTLocation mockASTLocation(final TargetProject project) {
    final ASTLocation astLocation = mock(ASTLocation.class);
    when(astLocation.getSourcePath()).thenReturn(project.getProductSourcePaths()
        .get(0));
    when(astLocation.inferLineNumbers()).thenReturn(ASTLocation.NONE);

    return astLocation;
  }

  private ASTNode createASTNode(final TargetProject project) {
    final GeneratedSourceCode originalSourceCode = TestUtil.createGeneratedSourceCode(project);
    final GeneratedJDTAST<ProductSourcePath> ast =
        (GeneratedJDTAST<ProductSourcePath>) originalSourceCode.getProductAsts()
            .get(0);
    return (ASTNode) ast.getRoot()
        .types()
        .get(0);
  }

  private Variant createVariant(final Fitness fitness, final TargetProject targetProject) {

    return new Variant(0, 0, new Gene(Collections.emptyList()),
        astConstruction.constructAST(targetProject), new EmptyTestResults("for testing."), fitness,
        Collections.emptyList(), new OriginalHistoricalElement());
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
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分はいらないのでNullやNullObjectにする
    // Baseのシリアライズに関するテストはここではしないのでASTLocationはモックにする
    final Operation operation = new InsertAfterOperation(createASTNode(project));
    final ASTLocation targetLocation = mockASTLocation(project);
    final Base appendBase = new Base(targetLocation, operation);
    final HistoricalElement historicalElement =
        new MutationHistoricalElement(initialVariant, appendBase);

    final JsonObject serializedHistoricalElement = gson.toJsonTree(historicalElement)
        .getAsJsonObject();

    // キーの存在チェック
    final Set<String> serializedOperationKey = serializedHistoricalElement.keySet();
    assertThat(serializedOperationKey).containsOnly(
        JsonKeyAlias.MutationHistoricalElement.PARENT_IDS,
        JsonKeyAlias.MutationHistoricalElement.NAME,
        JsonKeyAlias.MutationHistoricalElement.APPEND_BASE);

    // 親IDのチェック
    final JsonArray serializedParentIds =
        serializedHistoricalElement.get(JsonKeyAlias.MutationHistoricalElement.PARENT_IDS)
            .getAsJsonArray();
    assertThat(serializedParentIds).hasSize(1);
    assertThat(serializedParentIds.get(0)
        .getAsString()).isEqualTo(String.valueOf(0L));

    // 操作名のチェック
    final String operationName =
        serializedHistoricalElement.get(JsonKeyAlias.MutationHistoricalElement.NAME)
            .getAsString();
    assertThat(operationName).isEqualTo("insert_after");
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
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分はいらないのでNullやNullObjectにする
    final Operation operation = new DeleteOperation();
    final ASTLocation targetLocation = mockASTLocation(project);
    final Base appendBase = new Base(targetLocation, operation);
    final HistoricalElement historicalElement =
        new MutationHistoricalElement(initialVariant, appendBase);

    final JsonObject serializedHistoricalElement = gson.toJsonTree(historicalElement)
        .getAsJsonObject();

    // キーの存在チェック
    final Set<String> serializedOperationKey = serializedHistoricalElement.keySet();
    assertThat(serializedOperationKey).containsOnly(
        JsonKeyAlias.MutationHistoricalElement.PARENT_IDS,
        JsonKeyAlias.MutationHistoricalElement.NAME,
        JsonKeyAlias.MutationHistoricalElement.APPEND_BASE);

    // 親IDのチェック
    final JsonArray serializedParentIds =
        serializedHistoricalElement.get(JsonKeyAlias.MutationHistoricalElement.PARENT_IDS)
            .getAsJsonArray();
    assertThat(serializedParentIds).hasSize(1);
    assertThat(serializedParentIds.get(0)
        .getAsString()).isEqualTo(String.valueOf(0L));

    // 操作名のチェック
    final String operationName =
        serializedHistoricalElement.get(JsonKeyAlias.MutationHistoricalElement.NAME)
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
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant initialVariant = createVariant(new SimpleFitness(0.0d), project);

    // 差分はいらないのでNullやNullObjectにする
    final Operation operation = new ReplaceOperation(createASTNode(project));
    final ASTLocation targetLocation = mockASTLocation(project);
    final Base appendBase = new Base(targetLocation, operation);

    final HistoricalElement historicalElement =
        new MutationHistoricalElement(initialVariant, appendBase);

    final JsonObject serializedHistoricalElement = gson.toJsonTree(historicalElement)
        .getAsJsonObject();

    // キーの存在チェック
    final Set<String> serializedOperationKey = serializedHistoricalElement.keySet();
    assertThat(serializedOperationKey).containsOnly(
        JsonKeyAlias.MutationHistoricalElement.PARENT_IDS,
        JsonKeyAlias.MutationHistoricalElement.NAME,
        JsonKeyAlias.MutationHistoricalElement.APPEND_BASE);

    // 親IDのチェック
    final JsonArray serializedParentIds =
        serializedHistoricalElement.get(JsonKeyAlias.MutationHistoricalElement.PARENT_IDS)
            .getAsJsonArray();
    assertThat(serializedParentIds).hasSize(1);
    assertThat(serializedParentIds.get(0)
        .getAsString()).isEqualTo(String.valueOf(0L));

    // 操作名のチェック
    final String operationName =
        serializedHistoricalElement.get(JsonKeyAlias.MutationHistoricalElement.NAME)
            .getAsString();
    assertThat(operationName).isEqualTo("replace");
  }
}
