package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;

public class CrossoverHistoricalElementSerializerTest {

  private Gson gson;
  private final JDTASTConstruction astConstruction = new JDTASTConstruction();

  @Before
  public void setup() {
    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class,
            new CrossoverHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
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
   * Variantがシリアライズされているかテストする(交叉)
   *
   * 生成されるパッチのテスト・テスト結果のテストはしない
   */
  @Test
  public void testCrossover() {
    // 初期Variant
    final Path rootPath = Paths.get("example/CloseToZero01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
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

    final JsonObject serializedHistoricalElement = gson.toJsonTree(historicalElement)
        .getAsJsonObject();

    // キーの存在チェック
    final Set<String> serializedOperationKey = serializedHistoricalElement.keySet();
    assertThat(serializedOperationKey).containsOnly(
        JsonKeyAlias.CrossoverHistoricalElement.PARENT_IDS,
        JsonKeyAlias.CrossoverHistoricalElement.NAME,
        JsonKeyAlias.CrossoverHistoricalElement.CROSSOVER_POINT);

    // 親IDのチェック
    final JsonArray serializedParentIds = serializedHistoricalElement.get(
        JsonKeyAlias.CrossoverHistoricalElement.PARENT_IDS)
        .getAsJsonArray();
    final String[] parentIds = gson.fromJson(serializedParentIds, String[].class);
    assertThat(parentIds).hasSize(2);
    assertThat(parentIds).containsOnly(String.valueOf(1L), String.valueOf(2L));

    // 操作名のチェック
    final String operationName = serializedHistoricalElement.get(
        JsonKeyAlias.CrossoverHistoricalElement.NAME)
        .getAsString();
    assertThat(operationName).isEqualTo("crossover");
  }
}