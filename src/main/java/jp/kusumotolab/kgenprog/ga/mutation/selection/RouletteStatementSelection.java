package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.ProgramElementVisitor;
import jp.kusumotolab.kgenprog.project.jdt.StatementListVisitor;

/**
 * 再利用するステートメントを重みをつけたルーレットで選択するクラス
 *
 * @see RouletteSelection
 */
public class RouletteStatementSelection extends RouletteSelection {

  /**
   * コンストラクタ
   *
   * @param random 乱数生成器
   */
  public RouletteStatementSelection(final Random random) {
    super(random);
  }

  /**
   * ソースコードに含まれるステートメントを探索し，見つけたステートメントを保持する
   *
   * @param candidates 再利用するソースコードのリスト
   */
  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    final ProgramElementVisitor visitor = new StatementListVisitor();
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
    final ASTNode statement = reuseCandidate.getValue();
    final FullyQualifiedName fqn = reuseCandidate.getFqn();
    final StatementVisitor statementVisitor = new StatementVisitor(statement);
    final List<ReuseCandidate<Statement>> statements = statementVisitor.getStatements()
        .stream()
        .map(e -> new ReuseCandidate<>(e, fqn.getPackageName(), fqn))
        .collect(Collectors.toList());
    final int size = statements.size();
    return 1 / ((double) size);
  }
}
