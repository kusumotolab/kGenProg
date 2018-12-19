package jp.kusumotolab.kgenprog.ga.variant;

import java.util.List;

public interface HistoricalElement {

  /**
   * 親の遺伝子
   *
   * @return 親の遺伝子のList
   */
  public List<Variant> getParents();

  /**
   * @return 適用した操作の名前
   */
  public String getOperationName();
}
