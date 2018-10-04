package jp.kusumotolab.kgenprog.ga;

import java.util.List;

public interface HistoricalElement {

  /**
   * 親の遺伝子
   *
   * @return
   */
  public List<Variant> getParents();

}
