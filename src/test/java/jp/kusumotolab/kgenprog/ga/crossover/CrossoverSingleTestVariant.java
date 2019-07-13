package jp.kusumotolab.kgenprog.ga.crossover;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.test.TestResults;

// 1つの疑似バリアントからなるテストデータ．
//
// 10のテストケースが存在し，奇数番目のテストを失敗する．
// 4つのBaseを持ち，none，none，insert，insertである．
public class CrossoverSingleTestVariant {

  final Base noneBase;
  final Base insertBase;
  final Variant variant;
  final VariantStore variantStore;

  public CrossoverSingleTestVariant() {

    final TestResults testResultsA = Mockito.mock(TestResults.class);
    when(testResultsA.getFailedTestFQNs())
        .thenReturn(Arrays.asList(new TestFullyQualifiedName("Test1"),
            new TestFullyQualifiedName("Test3"), new TestFullyQualifiedName("Test5"),
            new TestFullyQualifiedName("Test7"), new TestFullyQualifiedName("Test9")));

    noneBase = new Base(null, new NoneOperation());
    insertBase = new Base(null, new InsertAfterOperation(null));
    final Gene geneA = new Gene(Arrays.asList(noneBase, noneBase, insertBase, insertBase));

    final Fitness fitnessA = new SimpleFitness(0.5d);

    variant = Mockito.mock(Variant.class);
    when(variant.getId()).thenReturn(0l);
    when(variant.getTestResults()).thenReturn(testResultsA);
    when(variant.getGene()).thenReturn(geneA);
    when(variant.getFitness()).thenReturn(fitnessA);

    variantStore = Mockito.mock(VariantStore.class);
    when(variantStore.getCurrentVariants()).thenReturn(Arrays.asList(variant));
    when(variantStore.createVariant(any(), any())).thenAnswer(invocation -> {
      final Gene gene = invocation.getArgument(0);
      final HistoricalElement element = invocation.getArgument(1);
      return new Variant(0, 0, gene, null, null, null, null, element);
    });
  }
}
