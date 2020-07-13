package jp.kusumotolab.kgenprog.ga.crossover;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
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
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

// 4つの疑似バリアントからなるテストデータ．
//
// 10のテストケースが存在すると仮定する．
// バリアントAは奇数番目のテストを失敗する．
// バリアントBは偶数番目のテストを失敗する．
// バリアントCは1〜6のテストを失敗する．
// バリアントDは5〜10のテストを失敗する．
//
// 各バリアントは4つのBaseを持つ．
// バリアントAは，none, none, none, none．
// バリアントBは，none, none, none, insert．
// バリアントCは，none, insert, insert, insert．
// バリアントDは，insert, insert, insert, insert．
public class CrossoverTestVariants {

  final Base noneBase;
  final Base insertBase;
  final Variant variantA;
  final Variant variantB;
  final Variant variantC;
  final Variant variantD;
  final VariantStore variantStore;

  public CrossoverTestVariants() {

    final TestResult succeededTestResult = Mockito.mock(TestResult.class);
    final TestResult failedTestResult = Mockito.mock(TestResult.class);

    try {
      Class<?> c = Class.forName("jp.kusumotolab.kgenprog.project.test.TestResult");
      Field f = c.getDeclaredField("failed");
      f.setAccessible(true);
      f.set(succeededTestResult, false);
      f.set(failedTestResult, true);
    } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
        | IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }

    final TestResults testResultsA = Mockito.mock(TestResults.class);
    when(testResultsA.getSucceededTestFQNs())
        .thenReturn(Arrays.asList(new TestFullyQualifiedName("Test2"),
            new TestFullyQualifiedName("Test4"), new TestFullyQualifiedName("Test6"),
            new TestFullyQualifiedName("Test8"), new TestFullyQualifiedName("Test10")));
    when(testResultsA.getFailedTestFQNs())
        .thenReturn(Arrays.asList(new TestFullyQualifiedName("Test1"),
            new TestFullyQualifiedName("Test3"), new TestFullyQualifiedName("Test5"),
            new TestFullyQualifiedName("Test7"), new TestFullyQualifiedName("Test9")));
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test1")))
        .thenReturn(failedTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test2")))
        .thenReturn(succeededTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test3")))
        .thenReturn(failedTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test4")))
        .thenReturn(succeededTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test5")))
        .thenReturn(failedTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test6")))
        .thenReturn(succeededTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test7")))
        .thenReturn(failedTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test8")))
        .thenReturn(succeededTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test9")))
        .thenReturn(failedTestResult);
    when(testResultsA.getTestResult(new TestFullyQualifiedName("Test10")))
        .thenReturn(succeededTestResult);

    final TestResults testResultsB = Mockito.mock(TestResults.class);
    when(testResultsB.getSucceededTestFQNs())
        .thenReturn(Arrays.asList(new TestFullyQualifiedName("Test1"),
            new TestFullyQualifiedName("Test3"), new TestFullyQualifiedName("Test5"),
            new TestFullyQualifiedName("Test7"), new TestFullyQualifiedName("Test9")));
    when(testResultsB.getFailedTestFQNs())
        .thenReturn(Arrays.asList(new TestFullyQualifiedName("Test2"),
            new TestFullyQualifiedName("Test4"), new TestFullyQualifiedName("Test6"),
            new TestFullyQualifiedName("Test8"), new TestFullyQualifiedName("Test10")));
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test1")))
        .thenReturn(succeededTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test2")))
        .thenReturn(failedTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test3")))
        .thenReturn(succeededTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test4")))
        .thenReturn(failedTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test5")))
        .thenReturn(succeededTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test6")))
        .thenReturn(failedTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test7")))
        .thenReturn(succeededTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test8")))
        .thenReturn(failedTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test9")))
        .thenReturn(succeededTestResult);
    when(testResultsB.getTestResult(new TestFullyQualifiedName("Test10")))
        .thenReturn(failedTestResult);

    final TestResults testResultsC = Mockito.mock(TestResults.class);
    when(testResultsC.getSucceededTestFQNs()).thenReturn(
        Arrays.asList(new TestFullyQualifiedName("Test7"), new TestFullyQualifiedName("Test8"),
            new TestFullyQualifiedName("Test9"), new TestFullyQualifiedName("Test10")));
    when(testResultsC.getFailedTestFQNs()).thenReturn(
        Arrays.asList(new TestFullyQualifiedName("Test1"), new TestFullyQualifiedName("Test2"),
            new TestFullyQualifiedName("Test3"), new TestFullyQualifiedName("Test4"),
            new TestFullyQualifiedName("Test5"), new TestFullyQualifiedName("Test6")));
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test1")))
        .thenReturn(failedTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test2")))
        .thenReturn(failedTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test3")))
        .thenReturn(failedTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test4")))
        .thenReturn(failedTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test5")))
        .thenReturn(failedTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test6")))
        .thenReturn(failedTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test7")))
        .thenReturn(succeededTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test8")))
        .thenReturn(succeededTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test9")))
        .thenReturn(succeededTestResult);
    when(testResultsC.getTestResult(new TestFullyQualifiedName("Test10")))
        .thenReturn(succeededTestResult);

    final TestResults testResultsD = Mockito.mock(TestResults.class);
    when(testResultsD.getSucceededTestFQNs()).thenReturn(
        Arrays.asList(new TestFullyQualifiedName("Test1"), new TestFullyQualifiedName("Test2"),
            new TestFullyQualifiedName("Test3"), new TestFullyQualifiedName("Test4")));
    when(testResultsD.getFailedTestFQNs()).thenReturn(
        Arrays.asList(new TestFullyQualifiedName("Test5"), new TestFullyQualifiedName("Test6"),
            new TestFullyQualifiedName("Test7"), new TestFullyQualifiedName("Test8"),
            new TestFullyQualifiedName("Test9"), new TestFullyQualifiedName("Test10")));
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test1")))
        .thenReturn(succeededTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test2")))
        .thenReturn(succeededTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test3")))
        .thenReturn(succeededTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test4")))
        .thenReturn(succeededTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test5")))
        .thenReturn(failedTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test6")))
        .thenReturn(failedTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test7")))
        .thenReturn(failedTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test8")))
        .thenReturn(failedTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test9")))
        .thenReturn(failedTestResult);
    when(testResultsD.getTestResult(new TestFullyQualifiedName("Test10")))
        .thenReturn(failedTestResult);

    noneBase = new Base(null, new NoneOperation());
    insertBase = new Base(null, new InsertAfterOperation(null));
    final Gene geneA = new Gene(Arrays.asList(noneBase, noneBase, noneBase, noneBase));
    final Gene geneB = new Gene(Arrays.asList(noneBase, noneBase, noneBase, insertBase));
    final Gene geneC = new Gene(Arrays.asList(noneBase, insertBase, insertBase, insertBase));
    final Gene geneD = new Gene(Arrays.asList(insertBase, insertBase, insertBase, insertBase));

    final Fitness fitnessA = new SimpleFitness(0.5d);
    final Fitness fitnessB = new SimpleFitness(0.5d);
    final Fitness fitnessC = new SimpleFitness(0.4d);
    final Fitness fitnessD = new SimpleFitness(0.4d);

    variantA = Mockito.mock(Variant.class);
    when(variantA.getId()).thenReturn(0l);
    when(variantA.getTestResults()).thenReturn(testResultsA);
    when(variantA.getGene()).thenReturn(geneA);
    when(variantA.getFitness()).thenReturn(fitnessA);
    variantB = Mockito.mock(Variant.class);
    when(variantB.getId()).thenReturn(1l);
    when(variantB.getTestResults()).thenReturn(testResultsB);
    when(variantB.getGene()).thenReturn(geneB);
    when(variantB.getFitness()).thenReturn(fitnessB);
    variantC = Mockito.mock(Variant.class);
    when(variantC.getId()).thenReturn(2l);
    when(variantC.getTestResults()).thenReturn(testResultsC);
    when(variantC.getGene()).thenReturn(geneC);
    when(variantC.getFitness()).thenReturn(fitnessC);
    variantD = Mockito.mock(Variant.class);
    when(variantD.getId()).thenReturn(3l);
    when(variantD.getTestResults()).thenReturn(testResultsD);
    when(variantD.getGene()).thenReturn(geneD);
    when(variantD.getFitness()).thenReturn(fitnessD);

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
