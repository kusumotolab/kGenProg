package jp.kusumotolab.kgenprog.ga.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 次世代に残す Variant を選択するクラス
 *
 * @see VariantSelection
 */
public class DefaultVariantSelection implements VariantSelection {

  private final int maxVariantsPerGeneration;
  private final Random random;

  /**
   * コンストラクタ
   *
   * @param maxVariantPerGeneration 次の世代にいくつの Variant を残すか
   * @param random 乱数生成器
   */
  public DefaultVariantSelection(final int maxVariantPerGeneration, final Random random) {
    this.maxVariantsPerGeneration = maxVariantPerGeneration;
    this.random = random;
  }

  /**
   * @param current 現在生き残っている Variant ののリスト
   * @param generated 今の世代で新しく生成された Variant のリスト
   * @return 次の世代に残す Variant のリスト
   */
  @Override
  public List<Variant> exec(final List<Variant> current, final List<Variant> generated) {
    final ArrayList<Variant> variants = new ArrayList<>(current);
    variants.addAll(generated);
    Collections.shuffle(variants, random);
    return variants.stream()
        .sorted(Comparator.comparing(Variant::getFitness)
            .reversed())
        .filter(Variant::isBuildSucceeded)
        .limit(maxVariantsPerGeneration)
        .collect(Collectors.toList());
  }
}
