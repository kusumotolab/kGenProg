package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

  @Test
  public void testExecForNan() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10);
    final List<Variant> variants = new ArrayList<>();

    final List<Variant> nanVariants = IntStream.range(0, 10)
        .mapToObj(e -> new SimpleFitness(Double.NaN))
        .map(e -> new Variant(null, e, null))
        .collect(Collectors.toList());

    variants.addAll(nanVariants);

    final List<Variant> result1 = variantSelection.exec(variants);

    assertThat(result1).hasSize(10);

    final Variant normalVariant = new Variant(null, new SimpleFitness(0.5d), null);
    variants.add(normalVariant);
    final List<Variant> result2 = variantSelection.exec(variants);
    assertThat(result2).hasSize(10);
    assertThat(result2.get(0)).isEqualTo(normalVariant);
  }

  @Test
  public void testExecForNanCompare() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10);

    final List<Variant> nanVariants = IntStream.range(0, 100)
        .mapToObj(e -> {
          if (e == 50) {
            return new SimpleFitness(SimpleFitness.MAXIMUM_VALUE);
          }
          return new SimpleFitness(Double.NaN);
        })
        .map(e -> new Variant(null, e, null))
        .collect(Collectors.toList());

    try {
      final List<Variant> result = variantSelection.exec(nanVariants);
      assertThat(result).hasSize(10);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
