package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.ProgramElementVisitor;
import jp.kusumotolab.kgenprog.project.jdt.StatementAndConditionVisitor;

/**
 * Implementing a roulette selection for reuse of statements and conditions
 * 再利用する文もしくは条件式を重みをつけたルーレットで選択するクラス
 *
 * @see StatementAndConditionSelection
 */
public class RouletteStatementAndConditionSelection extends RouletteSelection {

  /**
   * コンストラクタ
   *
   * @param random 乱数生成器
   */
  public RouletteStatementAndConditionSelection(final Random random) {
    super(random);
  }

  /**
   * ソースコードに含まれるステートメントを探索し，見つけたステートメントを保持する
   *
   * @param candidates 再利用するソースコードのリスト
   */
  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    final ProgramElementVisitor visitor = new StatementAndConditionVisitor();
    super.setCandidates(candidates, visitor);
  }

  /**
   * 各ステートメントの重みを計算するメソッド
   *
   * @param reuseCandidate 重みを計算したいステートメント
   * @return 重み
   */
  @Override
  public double getElementWeight(final ReuseCandidate<ASTNode> reuseCandidate) {
    return 1.0d;
  }
}
