package jp.kusumotolab.kgenprog.ga.validation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;

public class LimitedNumberFitnessTest {

  @Test
  public void testFitness() {

    // capacityが2のとき
    final LimitedNumberSimpleFitness fitness = new LimitedNumberSimpleFitness(1.0d, 2);
    final int capacity1 = fitness.getCapacity();
    assertThat(capacity1).isEqualTo(2);
    assertThat(fitness.getSingularValue()).isEqualTo(1.0d);
    assertThat(fitness.isMaximum()).isTrue();

    // capacityが2から1に減ったとき
    final int capacity2 = fitness.reduceCapacity();
    assertThat(capacity2).isEqualTo(1);
    assertThat(fitness.getSingularValue()).isEqualTo(1.0d);
    assertThat(fitness.isMaximum()).isTrue();

    // capacityが1から0に減ったとき
    final int capacity3 = fitness.reduceCapacity();
    assertThat(capacity3).isEqualTo(0);
    assertThat(fitness.getSingularValue()).isEqualTo(0d);
    assertThat(fitness.isMaximum()).isFalse();

    // capacityが0のときはこれ以上減らない
    final int capacity4 = fitness.reduceCapacity();
    assertThat(capacity4).isEqualTo(0);
    assertThat(fitness.getSingularValue()).isEqualTo(0);
    assertThat(fitness.isMaximum()).isFalse();
  }
}
