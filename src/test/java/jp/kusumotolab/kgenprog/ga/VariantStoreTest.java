package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.TestResults;


public class VariantStoreTest {

  @Test
  public void testCreateVariant() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final Configuration config = createMockConfiguration(basePath);

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
    assertThat(initialVariant.getGenerationNumber()).hasValue(0);

    final Gene gene = new Gene(Collections.emptyList());
    final HistoricalElement element = mock(HistoricalElement.class);
    final Variant variant = variantStore.createVariant(gene, element);

    assertThat(variant.getGenerationNumber()).hasValue(1);
    assertThat(variant.getGene()).isSameAs(gene);
    assertThat(variant.getGeneratedSourceCode()).isSameAs(sourceCodeGenerationResult);
    assertThat(variant.getTestResults()).isSameAs(testExecutorResult);
    assertThat(variant.getFitness()).isSameAs(sourceCodeValidationResult);
    assertThat(variant.getHistoricalElement()).isSameAs(element);

    // 世代が進んだときのVariant.getGenerationNumberを確認
    variantStore.proceedNextGeneration();
    final Variant variant2g = variantStore.createVariant(gene, element);
    assertThat(variant2g.getGenerationNumber()).hasValue(2);

    variantStore.proceedNextGeneration();
    final Variant variant3g = variantStore.createVariant(gene, element);
    assertThat(variant3g.getGenerationNumber()).hasValue(3);
  }


  @Test
  public void testGetGenerationNumber() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final Configuration config = createMockConfiguration(basePath);
    final Strategies strategies = mock(Strategies.class);
    when(strategies.execVariantSelection(any(), any())).thenReturn(Collections.emptyList());

    final VariantStore variantStore = new VariantStore(config, strategies);

    // 初期世代番号は1
    assertThat(variantStore.getGenerationNumber()
        .get()).isEqualTo(1);

    // setNextGenerationVariantsするたびに1増える
    variantStore.proceedNextGeneration();
    assertThat(variantStore.getGenerationNumber()
        .get()).isEqualTo(2);

    variantStore.proceedNextGeneration();
    assertThat(variantStore.getGenerationNumber()
        .get()).isEqualTo(3);

  }

  @Test
  public void testGetGeneratedVariants() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final Configuration config = createMockConfiguration(basePath);
    final Strategies strategies = mock(Strategies.class);
    when(strategies.execVariantSelection(any(), any())).then(v -> v.getArgument(1));

    final VariantStore variantStore = new VariantStore(config, strategies);

    final Variant success1 = createMockVariant(true);
    final Variant success2 = createMockVariant(true);
    final Variant success3 = createMockVariant(true);
    final Variant fail1 = createMockVariant(false);
    final Variant fail2 = createMockVariant(false);
    final Variant fail3 = createMockVariant(false);

    variantStore.addGeneratedVariant(success1);
    variantStore.addGeneratedVariant(fail1);
    variantStore.addGeneratedVariants(Arrays.asList(success2, fail2));
    variantStore.addGeneratedVariants(fail3, success3);

    // テスト成功Variantのみが含まれているか確認
    assertThat(variantStore.getFoundSolutionsNumber()).hasValue(3);
    assertThat(variantStore.getFoundSolutions()).containsExactly(success1, success2, success3);

    // テスト成功Variantが含まれていないか確認
    assertThat(variantStore.getGeneratedVariants()).containsExactly(fail1, fail2, fail3);

    variantStore.proceedNextGeneration();

    // 世代交代が行われたか確認
    assertThat(variantStore.getCurrentVariants()).containsExactly(fail1, fail2, fail3);
  }

  @Test
  public void testGetFoundSolutions() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final Configuration config = createMockConfiguration(basePath);
    final Strategies strategies = mock(Strategies.class);
    final VariantStore variantStore = new VariantStore(config, strategies);

    final Variant success1 = createMockVariant(true);
    final Variant success2 = createMockVariant(true);
    final Variant success3 = createMockVariant(true);

    variantStore.addGeneratedVariants(success1, success2, success3);

    // 数の指定なしの場合，すべて取得
    assertThat(variantStore.getFoundSolutions()).containsExactly(success1, success2, success3);

    // 数の指定した場合，その数だけ取得
    assertThat(variantStore.getFoundSolutions(2)).containsExactly(success1, success2);

    // 指定された数が見つかった数よりも多い場合，すべて取得
    assertThat(variantStore.getFoundSolutions(5)).containsExactly(success1, success2, success3);
  }

  private Variant createMockVariant(final boolean isCompleted) {
    final Variant variant = mock(Variant.class);
    when(variant.isCompleted()).thenReturn(isCompleted);
    return variant;
  }

  private Configuration createMockConfiguration(final Path basePath) {
    final TargetProject project = TargetProjectFactory.create(basePath);
    final Configuration config = mock(Configuration.class);

    when(config.getTargetProject()).thenReturn(project);
    when(config.getTimeLimitSeconds())
        .thenReturn(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());

    return config;
  }
}
