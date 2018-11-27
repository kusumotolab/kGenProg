package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Base;
import jp.kusumotolab.kgenprog.ga.Fitness;
import jp.kusumotolab.kgenprog.ga.Gene;
import jp.kusumotolab.kgenprog.ga.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;

public class VariantStoreSerializerTest {

  private Gson createGson(final Configuration config) {
    return new GsonBuilder().registerTypeAdapter(VariantStore.class,
        new VariantStoreSerializer(config))
        .registerTypeAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeAdapter(Patch.class, new PatchSerializer())
        .registerTypeAdapter(FileDiff.class, new FileDiffSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .create();
  }

  @Test
  public void testVariantStoreSerializer() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(project)
        .build();
    // gsonのセットアップ
    final Gson gson = createGson(config);

    final List<Suspiciousness> faultLocalizationResult = new ArrayList<>();
    final GeneratedSourceCode sourceCodeGenerationResult =
        new GeneratedSourceCode(Collections.emptyList(), Collections.emptyList());
    final TestResults testExecutorResult = mock(TestResults.class);
    final Fitness sourceCodeValidationResult = new SimpleFitness(Double.NaN);
    final GeneratedSourceCode astConstructionResult =
        new GeneratedSourceCode(Collections.emptyList(), Collections.emptyList());
    final Strategies strategies = mock(Strategies.class);
    when(strategies.execFaultLocalization(any(), any())).thenReturn(faultLocalizationResult);
    when(strategies.execSourceCodeGeneration(any(), any())).thenReturn(sourceCodeGenerationResult);
    when(strategies.execTestExecutor(any())).thenReturn(testExecutorResult);
    when(strategies.execSourceCodeValidation(any(), any())).thenReturn(sourceCodeValidationResult);
    when(strategies.execASTConstruction(any())).thenReturn(astConstructionResult);
    when(strategies.execVariantSelection(any(), any())).thenReturn(Collections.emptyList());

    final VariantStore variantStore = new VariantStore(config, strategies);
    final Variant initialVariant = variantStore.getInitialVariant();
    final Gene gene = new Gene(Collections.emptyList());
    final HistoricalElement element = new MutationHistoricalElement(initialVariant,
        new Base(null, new InsertOperation(null)));

    for (int i = 0; i < 10; i++) {
      final Variant variant = variantStore.createVariant(gene, element);
      variantStore.addGeneratedVariant(variant);
      variantStore.proceedNextGeneration();
    }

    final JsonObject serializedVariantStore = gson.toJsonTree(variantStore)
        .getAsJsonObject();
    // キーのチェック
    assertThat(serializedVariantStore.keySet()).containsOnly(JsonKeyAlias.VariantStore.PROJECT_NAME,
        JsonKeyAlias.VariantStore.VARIANTS);

    // 値のチェック
    final String projectName = serializedVariantStore.get(JsonKeyAlias.VariantStore.PROJECT_NAME)
        .getAsString();
    assertThat(projectName).isEqualTo("BuildSuccess01");

    final JsonArray serializedVariants = serializedVariantStore.get(
        JsonKeyAlias.VariantStore.VARIANTS)
        .getAsJsonArray();
    assertThat(serializedVariants).hasSize(11);
  }
}
