package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Arrays;
import java.util.List;

/**
 * 交叉の生成を記録するクラス
 */
public class CrossoverHistoricalElement implements HistoricalElement {

  private final Variant parentA;
  private final Variant parentB;
  private final int crossoverPoint;

  public CrossoverHistoricalElement(final Variant parentA, final Variant parentB,
      final int crossoverPoint) {
    this.parentA = parentA;
    this.parentB = parentB;
    this.crossoverPoint = crossoverPoint;
  }

  /**
   * @return 親となる Variant を返す
   */
  @Override
  public List<Variant> getParents() {
    return Arrays.asList(parentA, parentB);
  }

  /**
   * @return 適用された操作 (crossover)
   */
  @Override
  public String getOperationName() {
    return "crossover";
  }

  /**
   * @return 何世代目で交叉したか返す
   */
  public int getCrossoverPoint() {
    return crossoverPoint;
  }
}
