package jp.kusumotolab.kgenprog.ga.variant;

import java.util.ArrayList;
import java.util.List;

/**
 * 遺伝子情報を保持するクラス
 * 遺伝子情報は複数の塩基配列( Base のリスト) から成り立つ
 *
 * @see Base
 */
public class Gene {

  private final List<Base> bases;

  public Gene(final List<Base> bases) {
    this.bases = bases;
  }

  /**
   * @return 塩基配列を返す
   */
  public List<Base> getBases() {
    return bases;
  }

  /**
   * 引数で与えられた Base のリストのそれぞれをこのクラスの bases に追加して新しい Gene のリストを返すメソッド
   * TODO: テストでしか使っていないので消す
   *
   * @param bases 次世代の塩基列
   * @return 新しい Gene のリスト
   */
  public List<Gene> generateNextGenerationGenes(final List<Base> bases) {
    final List<Gene> genes = new ArrayList<>();
    for (final Base base : bases) {
      final List<Base> newBases = new ArrayList<>(this.bases);
      newBases.add(base);
      genes.add(new Gene(newBases));
    }
    return genes;
  }
}
