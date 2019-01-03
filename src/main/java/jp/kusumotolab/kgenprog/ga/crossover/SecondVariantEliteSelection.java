package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Comparator;
import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 評価関数に基づきバリアントを選択する戦略． 一つ目のバリアントと被らないように選択する．
 *
 */
public class SecondVariantEliteSelection implements SecondVariantSelectionStrategy {

  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant) {
    return variants.stream()
        .sorted(Comparator.comparing(Variant::getFitness)
            .reversed())
        .filter(v -> v != firstVariant) // TODO 本来は Variantにequalsメソッドを定義すべき？
        .findFirst()
        .get();
  }
}
