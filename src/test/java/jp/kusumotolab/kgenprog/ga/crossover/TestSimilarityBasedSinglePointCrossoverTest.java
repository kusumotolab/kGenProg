package jp.kusumotolab.kgenprog.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.TestFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class TestSimilarityBasedSinglePointCrossoverTest {

  private static Random random;
  private static Variant variantA;
  private static Variant variantB;
  private static Variant variantC;
  private static Variant variantD;

  @Before
  public void setup() {
    random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(1);

    // 10のテストケースが存在すると仮定する．
    // testResultsA は奇数番目のテストを失敗する
    // testResultsB は偶数番目のテストを失敗する
    // TestResultsC は1〜6のテストを失敗する
    // TestResultsD は5〜10のテストを失敗する
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

  @Test
  public void test_selectSecondVariant() {
    final SinglePointCrossover crossover = new TestSimilarityBasedSinglePointCrossover(random, 1);
    List<Variant> variants = Arrays.asList(variantA, variantB, variantC, variantD);

    final Variant variant1 = crossover.selectSecondVariant(variants, variantA);
    assertThat(variant1).isEqualTo(variantB);

    final Variant variant2 = crossover.selectSecondVariant(variants, variantC);
    assertThat(variant2).isEqualTo(variantD);
  }
}
