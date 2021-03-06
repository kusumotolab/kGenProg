package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Arrays;
import java.util.List;

/**
 * cascade交叉の記録をするクラス
 */
public class CascadeCrossoverHistoricalElement implements HistoricalElement {

  private final Variant parentA;
  private final Variant parentB;

  /**
   * @param parentA 交叉した片方の親
   * @param parentB もう片方の親
   */
  public CascadeCrossoverHistoricalElement(final Variant parentA, final Variant parentB) {
    this.parentA = parentA;
    this.parentB = parentB;
  }

  /**
   * @return 親のリスト
   */
  @Override
  public List<Variant> getParents() {
    return Arrays.asList(parentA, parentB);
  }

  /**
   * @return 適用した操作の名前
   */
  @Override
  public String getOperationName() {
    return "cascade-crossover";
  }
}
