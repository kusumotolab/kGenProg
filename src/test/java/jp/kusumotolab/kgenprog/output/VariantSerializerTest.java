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
import com.google.gson.JsonElement;
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
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;

public class VariantSerializerTest {

  private Gson gson;
  private final JDTASTConstruction astConstruction = new JDTASTConstruction();

  @Before
  public void setup() {
    gson = new GsonBuilder().registerTypeAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeAdapter(Patch.class, new PatchSerializer())
        .registerTypeAdapter(FileDiff.class, new FileDiffSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeHierarchyAdapter(MutationHistoricalElement.class,
            new MutationHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class,
            new CrossoverHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .create();
  }

  private Variant createVariant(final Fitness fitness, final TargetProject targetProject) {

    return new Variant(0, 0, new Gene(Collections.emptyList()),
        astConstruction.constructAST(targetProject), new EmptyTestResults("for testing."), fitness,
        Collections.emptyList(), new OriginalHistoricalElement());
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
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Variant variant = createVariant(new SimpleFitness(0.0d), project);

    final JsonObject serializedVariant = gson.toJsonTree(variant)
        .getAsJsonObject();

    // キーを持っているかチェック
    final Set<String> serializedVariantKey = serializedVariant.keySet();
    assertThat(serializedVariantKey).containsOnly(//
        JsonKeyAlias.Variant.ID, //
        JsonKeyAlias.Variant.FITNESS, //
        JsonKeyAlias.Variant.GENERATION_NUMBER, //
        JsonKeyAlias.Variant.IS_BUILD_SUCCESS, //
        JsonKeyAlias.Variant.OPERATION, //
        JsonKeyAlias.Variant.TEST_SUMMARY, //
        JsonKeyAlias.Variant.SELECTION_COUNT, //
        JsonKeyAlias.Variant.PATCH, //
        JsonKeyAlias.Variant.IS_SYNTAX_VALID, //
        JsonKeyAlias.Variant.BASES);

    // 各値のチェック
    final JsonElement id = serializedVariant.get(JsonKeyAlias.Variant.ID);
    final JsonElement fitness = serializedVariant.get(JsonKeyAlias.Variant.FITNESS);
    final JsonElement generationNumber =
        serializedVariant.get(JsonKeyAlias.Variant.GENERATION_NUMBER);
    final JsonElement isBuildSuccess = serializedVariant.get(JsonKeyAlias.Variant.IS_BUILD_SUCCESS);
    final JsonElement selectionCount = serializedVariant.get(JsonKeyAlias.Variant.SELECTION_COUNT);

    // TODO win+gradle 環境で落ちるのでいったんコメントアウト #389を解消してから戻すべき．
    // final JsonElement patches = serializedVariant.get(JsonKeyAlias.Variant.PATCH);

    final JsonElement bases = serializedVariant.get(JsonKeyAlias.Variant.BASES);
    final JsonElement is_syntax_valid = serializedVariant.get(JsonKeyAlias.Variant.IS_SYNTAX_VALID);

    assertThat(id.getAsLong()).isEqualTo(0);
    assertThat(fitness.getAsDouble()).isEqualTo(0.0d);
    assertThat(generationNumber.getAsInt()).isEqualTo(0);
    assertThat(isBuildSuccess.getAsBoolean()).isEqualTo(false);
    assertThat(selectionCount.getAsInt()).isEqualTo(0);

    // TODO 上のpatches宣言のコメントアウトに依存 #389を解消してから戻すべき．
    // assertThat(patches.getAsJsonArray()).isEmpty();

    assertThat(bases.getAsJsonArray()).isEmpty();
    assertThat(is_syntax_valid.getAsBoolean()).isEqualTo(true);
  }
}
