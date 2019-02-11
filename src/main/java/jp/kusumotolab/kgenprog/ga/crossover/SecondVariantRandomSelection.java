package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class SecondVariantRandomSelection implements SecondVariantSelectionStrategy {

  private final Random random;

  public SecondVariantRandomSelection(final Random random) {
    this.random = random;
  }

  /**
   * 一つ目の親以外のバリアントからランダムに選択
   * 
   */
  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant)
      throws CrossoverInfeasibleException {

    final List<Variant> secondVariantCandidates = variants.stream()
        .filter(v -> !v.equals(firstVariant))
        .collect(Collectors.toList());
    if (secondVariantCandidates.isEmpty()) { // 候補リストが空の時は例外を投げる
      throw new CrossoverInfeasibleException("no variant for second parent");
    }
    return secondVariantCandidates.get(random.nextInt(secondVariantCandidates.size()));
  }
}
