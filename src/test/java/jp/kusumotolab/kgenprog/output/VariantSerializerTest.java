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
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.OriginalHistoricalElement;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;

public class VariantSerializerTest {

  private Gson gson;
  private JDTASTConstruction astConstruction = new JDTASTConstruction();

  @Before
  public void setup() {
    gson = new GsonBuilder().registerTypeAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeAdapter(Patch.class, new PatchSerializer())
        .registerTypeAdapter(FileDiff.class, new FileDiffSerializer())
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
        JsonKeyAlias.Variant.PATCH);

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
    assertThat(isBuildSuccess).isEqualTo(false);

    final JsonArray serializedOperations = serializedVariant.get(JsonKeyAlias.Variant.OPERATIONS)
        .getAsJsonArray();
    assertThat(serializedOperations).isEmpty();

    final int selectionCount = serializedVariant.get(JsonKeyAlias.Variant.SELECTION_COUNT)
        .getAsInt();
    assertThat(selectionCount).isEqualTo(0);

    final JsonArray serializedPatches = serializedVariant.get(JsonKeyAlias.Variant.PATCH)
        .getAsJsonArray();
    assertThat(serializedPatches).isEmpty();
  }
}
