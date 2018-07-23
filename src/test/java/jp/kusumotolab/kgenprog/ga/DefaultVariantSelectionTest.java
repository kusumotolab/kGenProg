package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class DefaultVariantSelectionTest {

  @Test
  public void testExec() {
    final int variantSize = 5;
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(variantSize);
    final List<Variant> variants = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      final double divider = (i % 2 == 0) ? 10 : 20;
      final double value = (double) i / divider;
      final SimpleFitness fitness = new SimpleFitness(value);
      variants.add(new Variant(null, fitness, null));
    }
    final List<Variant> selectedVariants = variantSelection.exec(variants);

    assertThat(variants).hasSize(10)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getValue)
        .hasSize(10)
        .containsExactly(0.00d, 0.05d, 0.20d, 0.15d, 0.40d, 0.25d, 0.60d, 0.35d, 0.80d, 0.45d);

    assertThat(selectedVariants).hasSize(variantSize)
        .extracting(Variant::getFitness)
        .extracting(Fitness::getValue)
        .hasSize(5)
        .containsExactly(0.80d, 0.60d, 0.45d, 0.40d, 0.35d);
  }

  @Test
  public void testExecForEmptyVariants() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10);
    final List<Variant> variants = Collections.emptyList();
    final List<Variant> resultVariants = variantSelection.exec(variants);
    assertThat(resultVariants).hasSize(0);
  }
}
