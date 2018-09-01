package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.test.MockTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;


public class VariantStoreTest {

  
  private static class MockStrategies extends Strategies{
    private final List<Suspiciousness> faultLocalizationResult = new ArrayList<>();
    private final GeneratedSourceCode sourceCodeGenerationResult = new GeneratedSourceCode(Collections.emptyList());
    private final TestResults testExecutorResult = new MockTestResults();
    private final Fitness sourceCodeValidationResult = new SimpleFitness(Double.NaN);
    private final GeneratedSourceCode astConstructionResult = new GeneratedSourceCode(Collections.emptyList());
    
    
    public MockStrategies() {
      super(null, null, null, null, null);
    }

    @Override
    public List<Suspiciousness> execFaultLocalization(GeneratedSourceCode generatedSourceCode,
        TestResults testResults) {
      return faultLocalizationResult;
    }

    @Override
    public GeneratedSourceCode execSourceCodeGeneration(VariantStore variantStore, Gene gene) {
      return sourceCodeGenerationResult;
    }

    @Override
    public TestResults execTestExecutor(GeneratedSourceCode generatedSourceCode) {
      return testExecutorResult;
    }

    @Override
    public Fitness execSourceCodeValidation(VariantStore variantStore, TestResults testResults) {
      return sourceCodeValidationResult;
    }

    @Override
    public GeneratedSourceCode execASTConstruction(TargetProject targetProject) {
      return astConstructionResult;
    }
    
  }
  
  @Test
  public void testCreateVariant() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final MockStrategies strategies = new MockStrategies();
    final VariantStore variantStore = new VariantStore(project, strategies);
    final Gene gene = new SimpleGene(Collections.emptyList());
    
    final Variant variant = variantStore.createVariant(gene);
    assertThat(variant.getGene()).isSameAs(gene);
    assertThat(variant.getGeneratedSourceCode()).isSameAs(strategies.sourceCodeGenerationResult);
    assertThat(variant.getTestResults()).isSameAs(strategies.testExecutorResult);
    assertThat(variant.getFitness()).isSameAs(strategies.sourceCodeValidationResult);
  }

  @Test
  public void testGetGenerationNumber() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject project = TargetProjectFactory.create(basePath);
    final MockStrategies strategies = new MockStrategies();
    final VariantStore variantStore = new VariantStore(project, strategies);
    
    //初期世代番号は1
    assertThat(variantStore.getGenerationNumber().get()).isEqualTo(1);
    
    //setNextGenerationVariantsするたびに1増える
    variantStore.setNextGenerationVariants(Collections.emptyList());
    assertThat(variantStore.getGenerationNumber().get()).isEqualTo(2);
    
    variantStore.setNextGenerationVariants(Collections.emptyList());
    assertThat(variantStore.getGenerationNumber()
        .get()).isEqualTo(3);

  }
}
