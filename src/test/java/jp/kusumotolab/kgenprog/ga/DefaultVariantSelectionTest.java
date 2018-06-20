package jp.kusumotolab.kgenprog.ga;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

public class DefaultVariantSelectionTest {

  @Test
  public void execTest() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10);
    List<Variant> variants = new ArrayList<>();

    List<Variant> resultVariants = variantSelection.exec(variants);
    assertThat(resultVariants.size(), is(0));

    for (int i = 0; i < 100; i++) {
      final SimpleFitness fitness = i % 2 == 0 ?
          new SimpleFitness(((double) i) / 100) :
          new SimpleFitness(((double) i) / 200);
      variants.add(new Variant(null, fitness, null));
    }
    resultVariants = variantSelection.exec(variants);
    assertThat(resultVariants.size(), is(10));
    assertThat(resultVariants.get(0).getFitness().getValue(), is(closeTo(0.8, 0.000001)));
    assertThat(resultVariants.get(9).getFitness().getValue(), is(closeTo(0.98, 0.000001)));
  }
}
