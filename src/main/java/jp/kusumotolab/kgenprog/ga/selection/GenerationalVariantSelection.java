package jp.kusumotolab.kgenprog.ga.selection;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * Fitness が 1.0 のものを除いて残すクラス
 * TODO: DefaultVariantSelection に入れるべき機能
 *
 * @see DefaultVariantSelection
 */
public class GenerationalVariantSelection extends DefaultVariantSelection {

  /**
   * コンストラクタ
   *
   * @param maxVariantPerGeneration 1 世代に残す Variant の数
   * @param random 乱数生成器
   */
  public GenerationalVariantSelection(final int maxVariantPerGeneration, final Random random) {
    super(maxVariantPerGeneration, random);
  }

  /**
   * Fitness が 1.0 のものを除いて DefaultVariantSelection に処理を渡す
   *
   * @param current 現在生き残っている Variant のリスト
   * @param generated 今の世代で新しく生成された Variant のリスト
   * @return 残す Variant のリスト
   */
  @Override
  public List<Variant> exec(final List<Variant> current, final List<Variant> generated) {

    // 最後の世代のうち，Fitness が 1.0でないものを variantsForSelection に追加
    final List<Variant> variantsForSelection = current.stream()
        .filter(variant -> !variant.isCompleted())
        .collect(Collectors.toList());

    return super.exec(variantsForSelection, generated);
  }
}
