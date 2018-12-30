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

public class TestSimilarityBasedSinglePointCrossoverTest {

  private static Random random;
  private static CrossoverTestVariants testVariants;

  @Before
  public void setup() {
    random = Mockito.mock(Random.class);
    when(random.nextBoolean()).thenReturn(false);
    when(random.nextInt(anyInt())).thenReturn(1);

    testVariants = new CrossoverTestVariants();
  }

  @Test
  public void test_selectSecondVariant() {
    final SinglePointCrossover crossover = new TestSimilarityBasedSinglePointCrossover(random, 1);
    List<Variant> variants = Arrays.asList(testVariants.variantA, testVariants.variantB,
        testVariants.variantC, testVariants.variantD);

    final Variant variant1 = crossover.selectSecondVariant(variants, testVariants.variantA);
    assertThat(variant1).isEqualTo(testVariants.variantB);

    final Variant variant2 = crossover.selectSecondVariant(variants, testVariants.variantC);
    assertThat(variant2).isEqualTo(testVariants.variantD);
  }
}
