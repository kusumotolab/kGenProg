package jp.kusumotolab.kgenprog.ga.crossover;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * 4つの疑似バリアントからなるテストデータ．
 *
 * 10のテストケースが存在すると仮定する．
 * バリアントAは偶数番目のテストを失敗する．
 * バリアントBは奇数番目のテストを失敗する．
 * バリアントCは0〜4のテストを失敗する．
 * バリアントDは5〜9のテストを失敗する．
 *
 * 各バリアントは4つのBaseを持つ．
 * バリアントAは，none, none, none, none．
 * バリアントBは，none, none, none, insert．
 * バリアントCは，none, insert, insert, insert．
 * バリアントDは，insert, insert, insert, insert．
 */
public class CrossoverTestVariants {

  final Base noneBase;
  final Base insertBase;
  final Variant variantA;
  final Variant variantB;
  final Variant variantC;
  final Variant variantD;
  final VariantStore variantStore;

  final TestResult succeededTestResult = Mockito.mock(TestResult.class);
  final TestResult failedTestResult = Mockito.mock(TestResult.class);

  public CrossoverTestVariants() {

    try {
      Class<?> c = Class.forName("jp.kusumotolab.kgenprog.project.test.TestResult");
      Field f = c.getDeclaredField("failed");
      f.setAccessible(true);
      f.set(succeededTestResult, false);
      f.set(failedTestResult, true);
    } catch (final ClassNotFoundException | NoSuchFieldException | SecurityException
        | IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }

    final FullyQualifiedName test0 = new TestFullyQualifiedName("test0");
    final FullyQualifiedName test1 = new TestFullyQualifiedName("test1");
    final FullyQualifiedName test2 = new TestFullyQualifiedName("test2");
    final FullyQualifiedName test3 = new TestFullyQualifiedName("test3");
    final FullyQualifiedName test4 = new TestFullyQualifiedName("test4");
    final FullyQualifiedName test5 = new TestFullyQualifiedName("test5");
    final FullyQualifiedName test6 = new TestFullyQualifiedName("test6");
    final FullyQualifiedName test7 = new TestFullyQualifiedName("test7");
    final FullyQualifiedName test8 = new TestFullyQualifiedName("test8");
    final FullyQualifiedName test9 = new TestFullyQualifiedName("test9");

    final TestResults testResultsA = setupTestResults(
        Arrays.asList(test1, test3, test5, test7, test9),
        Arrays.asList(test0, test2, test4, test6, test8));
    final TestResults testResultsB = setupTestResults(
        Arrays.asList(test0, test2, test4, test6, test8),
        Arrays.asList(test1, test3, test5, test7, test9));
    final TestResults testResultsC = setupTestResults(
        Arrays.asList(test6, test7, test8, test9),
        Arrays.asList(test0, test1, test2, test3, test4, test5));
    final TestResults testResultsD = setupTestResults(
        Arrays.asList(test0, test1, test2, test3),
        Arrays.asList(test4, test5, test6, test7, test8, test9));

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
    when(variantA.getId()).thenReturn(0L);
    when(variantA.getTestResults()).thenReturn(testResultsA);
    when(variantA.getGene()).thenReturn(geneA);
    when(variantA.getFitness()).thenReturn(fitnessA);
    when(variantA.isCompleted()).thenReturn(false);
    variantB = Mockito.mock(Variant.class);
    when(variantB.getId()).thenReturn(1L);
    when(variantB.getTestResults()).thenReturn(testResultsB);
    when(variantB.getGene()).thenReturn(geneB);
    when(variantB.getFitness()).thenReturn(fitnessB);
    when(variantB.isCompleted()).thenReturn(false);
    variantC = Mockito.mock(Variant.class);
    when(variantC.getId()).thenReturn(2L);
    when(variantC.getTestResults()).thenReturn(testResultsC);
    when(variantC.getGene()).thenReturn(geneC);
    when(variantC.getFitness()).thenReturn(fitnessC);
    when(variantC.isCompleted()).thenReturn(false);
    variantD = Mockito.mock(Variant.class);
    when(variantD.getId()).thenReturn(3L);
    when(variantD.getTestResults()).thenReturn(testResultsD);
    when(variantD.getGene()).thenReturn(geneD);
    when(variantD.getFitness()).thenReturn(fitnessD);
    when(variantD.isCompleted()).thenReturn(false);

    variantStore = Mockito.mock(VariantStore.class);
    when(variantStore.getCurrentVariants())
        .thenReturn(Arrays.asList(variantA, variantB, variantC, variantD));
    when(variantStore.createVariant(any(), any())).thenAnswer(invocation -> {
      final Gene gene = invocation.getArgument(0);
      final HistoricalElement element = invocation.getArgument(1);
      return new Variant(0, 0, gene, null, null, new SimpleFitness(0.5), null, element);
    });
    when(variantStore.getFoundSolutionsNumber()).thenReturn(new OrdinalNumber(0));
  }

  private TestResults setupTestResults(final List<FullyQualifiedName> passes,
      final List<FullyQualifiedName> fails) {
    final TestResults results = Mockito.mock(TestResults.class);
    when(results.getSucceededTestFQNs()).thenReturn(passes);
    when(results.getFailedTestFQNs()).thenReturn(fails);
    passes.forEach(s -> when(results.getTestResult(s)).thenReturn(succeededTestResult));
    fails.forEach(s -> when(results.getTestResult(s)).thenReturn(failedTestResult));
    return results;
  }
}
