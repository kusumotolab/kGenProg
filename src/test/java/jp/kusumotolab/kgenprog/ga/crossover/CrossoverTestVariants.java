package jp.kusumotolab.kgenprog.ga.crossover;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.test.TestResults;

// 10のテストケースが存在すると仮定する．
// testResultsA は奇数番目のテストを失敗する．
// testResultsB は偶数番目のテストを失敗する．
// TestResultsC は1〜6のテストを失敗する．
// TestResultsD は5〜10のテストを失敗する．
public class CrossoverTestVariants {

  final Base noneBase;
  final Base insertBase;
  final Variant variantA;
  final Variant variantB;
  final Variant variantC;
  final Variant variantD;
  final VariantStore variantStore;

  public CrossoverTestVariants() {

    final TestResults testResultsA = Mockito.mock(TestResults.class);
    when(testResultsA.getFailedTestFQNs())
        .thenReturn(Arrays.asList(new TestFullyQualifiedName("Test1"),
            new TestFullyQualifiedName("Test3"), new TestFullyQualifiedName("Test5"),
            new TestFullyQualifiedName("Test7"), new TestFullyQualifiedName("Test9")));
    final TestResults testResultsB = Mockito.mock(TestResults.class);
    when(testResultsB.getFailedTestFQNs())
        .thenReturn(Arrays.asList(new TestFullyQualifiedName("Test2"),
            new TestFullyQualifiedName("Test4"), new TestFullyQualifiedName("Test6"),
            new TestFullyQualifiedName("Test8"), new TestFullyQualifiedName("Test10")));
    final TestResults testResultsC = Mockito.mock(TestResults.class);
    when(testResultsC.getFailedTestFQNs()).thenReturn(
        Arrays.asList(new TestFullyQualifiedName("Test1"), new TestFullyQualifiedName("Test2"),
            new TestFullyQualifiedName("Test3"), new TestFullyQualifiedName("Test4"),
            new TestFullyQualifiedName("Test5"), new TestFullyQualifiedName("Test6")));
    final TestResults testResultsD = Mockito.mock(TestResults.class);
    when(testResultsD.getFailedTestFQNs()).thenReturn(
        Arrays.asList(new TestFullyQualifiedName("Test5"), new TestFullyQualifiedName("Test6"),
            new TestFullyQualifiedName("Test7"), new TestFullyQualifiedName("Test8"),
            new TestFullyQualifiedName("Test9"), new TestFullyQualifiedName("Test10")));

    noneBase = new Base(null, new NoneOperation());
    insertBase = new Base(null, new InsertOperation(null));
    final Gene geneA = new Gene(Arrays.asList(noneBase, noneBase, noneBase, noneBase));
    final Gene geneB = new Gene(Arrays.asList(noneBase, noneBase, noneBase, insertBase));
    final Gene geneC = new Gene(Arrays.asList(noneBase, insertBase, insertBase, insertBase));
    final Gene geneD = new Gene(Arrays.asList(insertBase, insertBase, insertBase, insertBase));

    variantA = Mockito.mock(Variant.class);
    when(variantA.getTestResults()).thenReturn(testResultsA);
    when(variantA.getGene()).thenReturn(geneA);
    variantB = Mockito.mock(Variant.class);
    when(variantB.getTestResults()).thenReturn(testResultsB);
    when(variantB.getGene()).thenReturn(geneB);
    variantC = Mockito.mock(Variant.class);
    when(variantC.getTestResults()).thenReturn(testResultsC);
    when(variantC.getGene()).thenReturn(geneC);
    variantD = Mockito.mock(Variant.class);
    when(variantD.getTestResults()).thenReturn(testResultsD);
    when(variantD.getGene()).thenReturn(geneD);

    variantStore = Mockito.mock(VariantStore.class);
    when(variantStore.getCurrentVariants())
        .thenReturn(Arrays.asList(variantA, variantB, variantC, variantD));
    when(variantStore.createVariant(any(), any())).thenAnswer(invocation -> {
      final Gene gene = invocation.getArgument(0);
      final HistoricalElement element = invocation.getArgument(1);
      return new Variant(0, 0, gene, null, null, null, null, element);
    });
  }
}
