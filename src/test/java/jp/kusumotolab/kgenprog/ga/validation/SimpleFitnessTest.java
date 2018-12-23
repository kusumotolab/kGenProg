package jp.kusumotolab.kgenprog.ga.validation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;

public class SimpleFitnessTest {

  @Test
  public void testCompareTo01() {
    final SimpleFitness fitness1 = new SimpleFitness(1.0);
    final SimpleFitness fitness2 = new SimpleFitness(2.0);

    assertThat(fitness1.compareTo(fitness2)).isLessThan(0);
    assertThat(fitness2.compareTo(fitness1)).isGreaterThan(0);
  }

  @Test
  public void testCompareTo02() {
    final SimpleFitness fitness1 = new SimpleFitness(1.0);
    final SimpleFitness fitnessNaN = new SimpleFitness(Double.NaN);

    assertThat(fitness1.compareTo(fitnessNaN)).isGreaterThan(0);
    assertThat(fitnessNaN.compareTo(fitness1)).isLessThan(0);
  }

  @Test
  public void testCompareTo03() {
    final SimpleFitness fitnessNaN1 = new SimpleFitness(Double.NaN);
    final SimpleFitness fitnessNaN2 = new SimpleFitness(Double.NaN);

    assertThat(fitnessNaN1.compareTo(fitnessNaN2)).isEqualTo(0);
    assertThat(fitnessNaN2.compareTo(fitnessNaN1)).isEqualTo(0);
  }
}
