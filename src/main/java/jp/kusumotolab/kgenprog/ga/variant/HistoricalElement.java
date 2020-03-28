package jp.kusumotolab.kgenprog.ga.variant;

import java.util.List;

/**
 * Variant が生成される過程を記録するインターフェース
 */
public interface HistoricalElement {

  /**
   * 親の遺伝子
   *
   * @return 親の遺伝子のList
   */
  List<Variant> getParents();

  /**
   * @return 適用した操作の名前
   */
  String getOperationName();
}
