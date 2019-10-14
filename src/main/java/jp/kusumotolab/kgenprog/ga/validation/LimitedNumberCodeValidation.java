package jp.kusumotolab.kgenprog.ga.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 * テスト通過率をそのまま評価値とする評価方法．ただし評価の残り回数を過ぎると評価値を0にする．
 *
 */
public class LimitedNumberCodeValidation implements SourceCodeValidation {

  private static final int DEFAULT_CAPACITY = 100;
  private final int capacity;
  private final Map<List<Base>, LimitedNumberSimpleFitness> basesFitnessMap;

  /**
   * デフォルトの評価回数(100)で初期化
   */
  public LimitedNumberCodeValidation() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * 与えられた評価回数で初期化
   * 
   * @param capacity 評価回数
   */
  public LimitedNumberCodeValidation(final int capacity) {
    this.capacity = capacity;
    this.basesFitnessMap = new HashMap<>();
  }

  /**
   * @param input 評価値計算に利用する情報
   * @return 評価値
   */
  @Override
  public Fitness exec(final Input input) {
    final Gene targetGene = input.getGene();
    final List<Base> targetBases = targetGene.getBases();

    // 評価対象の遺伝子の評価値がすでに計算されているのであれば，再計算を避ける
    if (basesFitnessMap.containsKey(targetBases)) {
      return basesFitnessMap.get(targetBases);
    }

    final TestResults testResults = input.getTestResults();
    final double successRate = testResults.getSuccessRate();
    final List<Base> parentBases = getParentGene(targetGene);

    // 親の評価値がこの変異プログラムの評価値よりも高い場合は，親の評価値を下げる．
    // parentBasesが存在しているかどうかを確認している理由は，
    // 交叉により生成された変異プログラムについては現在対象外のため．
    // TODO 交叉により生成された変異プログラムへの対応
    if (basesFitnessMap.containsKey(parentBases)) {
      final LimitedNumberSimpleFitness parentFitness = basesFitnessMap.get(parentBases);
      if (successRate <= parentFitness.getValue()) {
        parentFitness.reduceCapacity();
      }
    }

    final LimitedNumberSimpleFitness fitness =
        new LimitedNumberSimpleFitness(successRate, capacity);
    basesFitnessMap.put(targetBases, fitness);

    return fitness;
  }

  // TODO 交叉の場合に対応できたらGeneクラスに移動すべき
  private List<Base> getParentGene(final Gene gene) {
    final List<Base> bases = new ArrayList<>(gene.getBases());
    bases.remove(bases.size() - 1);
    return bases;
  }
}
