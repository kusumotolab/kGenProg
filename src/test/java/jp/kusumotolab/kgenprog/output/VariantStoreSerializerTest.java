package jp.kusumotolab.kgenprog.output;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias;
import jp.kusumotolab.kgenprog.testutil.JsonKeyAlias.CrossoverHistoricalElement;

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
        .registerTypeHierarchyAdapter(MutationHistoricalElement.class,
            new MutationHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class,
            new CrossoverHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
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
    final Fitness sourceCodeValidationResult = new SimpleFitness(1.0d);
    final GeneratedSourceCode astConstructionResult =
        new GeneratedSourceCode(Collections.emptyList(), Collections.emptyList());
    final TestExecutor testExecutor = new LocalTestExecutor(config);
    final Strategies strategies = mock(Strategies.class);
    when(strategies.execFaultLocalization(any(), any())).thenReturn(faultLocalizationResult);
    when(strategies.execSourceCodeGeneration(any(), any())).thenReturn(sourceCodeGenerationResult);
    when(strategies.execTestExecutor(any())).thenReturn(testExecutorResult);
    when(strategies.execSourceCodeValidation(any(), any())).thenReturn(sourceCodeValidationResult);
    when(strategies.execASTConstruction(any())).thenReturn(astConstructionResult);
    when(strategies.execVariantSelection(any(), any())).thenReturn(Collections.emptyList());
    when(strategies.execAsyncTestExecutor(any())).then(v -> {
      final Single<Variant> variantSingle = v.getArgument(0);
      return testExecutor.execAsync(variantSingle);
    });

    final VariantStore variantStore = new VariantStore(config, strategies);
    final Variant initialVariant = variantStore.getInitialVariant();
    final Gene gene = new Gene(Collections.emptyList());

    // このテストはBaseのシリアライズをテストしないのでtargetLocationはモックにする
    final ASTLocation targetLocation = mock(ASTLocation.class);
    when(targetLocation.getSourcePath()).thenReturn(project.getProductSourcePaths()
        .get(0));
    when(targetLocation.inferLineNumbers()).thenReturn(ASTLocation.NONE);
    final HistoricalElement element = new MutationHistoricalElement(initialVariant,
        new Base(targetLocation, new DeleteOperation()));

    for (int i = 0; i < 10; i++) {
      final Variant variant = variantStore.createVariant(gene, element);
      variantStore.addGeneratedVariant(variant);
      variantStore.proceedNextGeneration();
    }

    final JsonObject serializedVariantStore = gson.toJsonTree(variantStore)
        .getAsJsonObject();
    // キーのチェック
    assertThat(serializedVariantStore.keySet()).containsOnly(
        JsonKeyAlias.VariantStore.PROJECT_NAME,
        JsonKeyAlias.VariantStore.VARIANTS,
        JsonKeyAlias.VariantStore.CONFIGURATION);

    // 値のチェック
    final String projectName = serializedVariantStore.get(JsonKeyAlias.VariantStore.PROJECT_NAME)
        .getAsString();
    assertThat(projectName).isEqualTo("BuildSuccess01");

    final JsonArray serializedVariants = serializedVariantStore.get(
        JsonKeyAlias.VariantStore.VARIANTS)
        .getAsJsonArray();
    assertThat(serializedVariants).hasSize(11);
  }

  @Test
  public void testConfigurationSerialization() {
    final Path rootPath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(rootPath);
    final Configuration config = new Configuration.Builder(project)
        .build();
    // gsonのセットアップ
    final Gson gson = createGson(config);

    final JsonObject serializedConfiguration = gson.toJsonTree(config)
        .getAsJsonObject();

    // キーのチェック
    final Class<?> clazz = config.getClass();
    final String[] configFieldNames = Arrays.stream(clazz.getDeclaredFields())
        .map(Field::getName)
        .filter(e -> !e.startsWith("DEFAULT_"))
        .toArray(String[]::new);
    assertThat(serializedConfiguration.keySet()).containsOnly(configFieldNames);
  }
}
