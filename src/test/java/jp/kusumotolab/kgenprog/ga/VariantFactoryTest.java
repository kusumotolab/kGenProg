package jp.kusumotolab.kgenprog.ga;

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
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class VariantFactoryTest {

  @Test
  public void testSequentialVariantFactory() {
    testExec(new SequentialVariantFactory());
  }

  @Test
  public void testLazyVariantFactory() {
    testExec(new LazyVariantFactory());
  }

  // 実装によらず VariantFactory の実行結果は同じ
  public void testExec(final VariantFactory variantFactory) {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final Configuration config = mock(Configuration.class);
    when(config.getTargetProject()).thenReturn(project);
    when(config.getTimeLimitSeconds())
        .thenReturn(Configuration.DEFAULT_TEST_TIME_LIMIT.getSeconds());

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
    when(strategies.execVariantFactory(any(), any(), any(), any(), any())).then(
        v -> variantFactory.exec(v.getArgument(0), v.getArgument(1), v.getArgument(2),
            v.getArgument(3), v.getArgument(4), strategies));

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
}
