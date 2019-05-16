package jp.kusumotolab.kgenprog.ga.selection;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 各世代の終了時に Variant を選択して残すインターフェース
 */
public interface VariantSelection {

  /**
   * @param current 現在生き残っている Variant のリスト
   * @param generated その世代で生成した Variant のリスト
   * @return 生き残らせる Variant のリスト
   */
  List<Variant> exec(List<Variant> current, List<Variant> generated);
}
