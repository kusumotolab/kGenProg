package jp.kusumotolab.kgenprog.ga.crossover;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TestResults;

// 10のテストケースが存在すると仮定する．
// testResultsA は奇数番目のテストを失敗する．
// testResultsB は偶数番目のテストを失敗する．
// TestResultsC は1〜6のテストを失敗する．
// TestResultsD は5〜10のテストを失敗する．
public class CrossoverTestVariants {

  final Variant variantA;
  final Variant variantB;
  final Variant variantC;
  final Variant variantD;

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
    variantA = Mockito.mock(Variant.class);
    when(variantA.getTestResults()).thenReturn(testResultsA);
    variantB = Mockito.mock(Variant.class);
    when(variantB.getTestResults()).thenReturn(testResultsB);
    variantC = Mockito.mock(Variant.class);
    when(variantC.getTestResults()).thenReturn(testResultsC);
    variantD = Mockito.mock(Variant.class);
    when(variantD.getTestResults()).thenReturn(testResultsD);
  }
}
