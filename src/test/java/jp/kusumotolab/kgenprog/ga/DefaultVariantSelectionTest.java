package jp.kusumotolab.kgenprog.ga;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class DefaultVariantSelectionTest {

  @Test
  public void execTest() {
    final DefaultVariantSelection variantSelection = new DefaultVariantSelection(10);
    List<Variant> variants = new ArrayList<>();

    List<Variant> resultVariants = variantSelection.exec(variants);
    assertEquals(0, resultVariants.size());

    for (int i = 0; i < 100; i++) {
      final SimpleFitness fitness = i % 2 == 0 ?
          new SimpleFitness(((double) i) / 100) :
          new SimpleFitness(((double) i) / 200);
      variants.add(new Variant(null, fitness, null));
    }
    resultVariants = variantSelection.exec(variants);
    assertEquals(10, resultVariants.size());
    assertEquals(0.8, resultVariants.get(0).getFitness().getValue(), 0.0);
    assertEquals(0.98, resultVariants.get(9).getFitness().getValue(), 0.0);
  }
}
