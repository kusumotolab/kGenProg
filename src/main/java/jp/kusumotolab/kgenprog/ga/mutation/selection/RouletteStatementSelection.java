package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * 再利用するステートメントを重みをつけたルーレットでで選択するクラス
 *
 * @see StatementSelection
 */
public class RouletteStatementSelection extends StatementSelection {

  /**
   * コンストラクタ
   *
   * @param random 乱数生成器
   */
  public RouletteStatementSelection(final Random random) {
    super(random);
  }

  /**
   * 各ステートメントの重みを計算するメソッド
   * 引数で与えられたステートメントに含まれるステートメントの数の逆数を返す
   * (直感的には小さいステートメントほど再利用されやすくなる)
   *
   * @param reuseCandidate 重みを計算したいステートメント
   * @return 重み
   */
  @Override
  public double getStatementWeight(final ReuseCandidate<Statement> reuseCandidate) {
    final Statement statement = reuseCandidate.getValue();
    final FullyQualifiedName fqn = reuseCandidate.getFqn();
    final StatementVisitor statementVisitor = new StatementVisitor(statement, fqn);
    final List<ReuseCandidate<Statement>> statements = statementVisitor.getReuseCandidateList();
    final int size = statements.size();
    return 1 / ((double) size);
  }
}
