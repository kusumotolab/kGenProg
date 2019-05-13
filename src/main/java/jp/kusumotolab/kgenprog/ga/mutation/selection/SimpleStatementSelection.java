package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.Random;
import org.eclipse.jdt.core.dom.Statement;

/**
 * 再利用するステートメントを乱数に基づいて選択する
 * (現在は RouletteStatementSelection を使用しているので使っていない)
 *
 * @see StatementSelection
 * @see RouletteStatementSelection
 */
public class SimpleStatementSelection extends StatementSelection {

  /**
   * コンストラクタ
   *
   * @param random 乱数生成器
   */
  public SimpleStatementSelection(final Random random) {
    super(random);
  }

  /**
   * 定数の重みを返すメソッド
   *
   * @param reuseCandidate 重みを計算したいステートメント
   * @return 重み(1.0d)
   */
  @Override
  public double getStatementWeight(final ReuseCandidate<Statement> reuseCandidate) {
    return 1.0d;
  }
}
