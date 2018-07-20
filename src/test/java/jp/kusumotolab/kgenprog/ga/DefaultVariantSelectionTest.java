package jp.kusumotolab.kgenprog.ga;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

public class DefaultVariantSelectionTest {

  @Test
  public void testExec() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10);
    final List<Variant> variants = new ArrayList<>();

    List<Variant> resultVariants = variantSelection.exec(variants);
    assertThat(resultVariants.size(), is(0));

    for (int i = 0; i < 100; i++) {
      final SimpleFitness fitness = i % 2 == 0 ? new SimpleFitness(((double) i) / 100)
          : new SimpleFitness(((double) i) / 200);
      variants.add(new Variant(null, fitness, null));
    }
    resultVariants = variantSelection.exec(variants);
    assertThat(resultVariants.size(), is(10));
    assertThat(resultVariants.get(0)
        .getFitness()
        .getValue(), is(closeTo(0.98, 0.000001)));
    assertThat(resultVariants.get(9)
        .getFitness()
        .getValue(), is(closeTo(0.8, 0.000001)));
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
    assertThat(result1.size(), is(0));

    final Variant normalVariant = new Variant(null, new SimpleFitness(0.5d), null);
    variants.add(normalVariant);
    final List<Variant> result2 = variantSelection.exec(variants);
    assertThat(result2.size(), is(1));
    assertThat(result2.get(0)
        .equals(normalVariant), is(true));
  }
}
