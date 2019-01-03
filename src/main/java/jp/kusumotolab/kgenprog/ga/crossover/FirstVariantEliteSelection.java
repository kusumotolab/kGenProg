package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 評価関数が最大のバリアントを選択する戦略． 最大のバリアントが複数ある場合は，それらの中からランダムに選択する．
 *
 */
public class FirstVariantEliteSelection implements FirstVariantSelectionStrategy {

  private final Random random;

  public FirstVariantEliteSelection(final Random random) {
    this.random = random;
  }

  @Override
  public Variant exec(final List<Variant> variants) {
    final Fitness maxFitness = variants.stream()
        .max(Comparator.comparing(Variant::getFitness))
        .get()
        .getFitness();
    final List<Variant> maxFitnessVariants = variants.stream()
        .filter(v -> 0 == maxFitness.compareTo(v.getFitness()))
        .collect(Collectors.toList());
    final int index = random.nextInt(maxFitnessVariants.size());
    return maxFitnessVariants.get(index);
  }
}
