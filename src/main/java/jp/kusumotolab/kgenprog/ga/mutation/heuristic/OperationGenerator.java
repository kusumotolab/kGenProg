package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.ga.mutation.AccessibleVariableSearcher;
import jp.kusumotolab.kgenprog.ga.mutation.Query;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.Variable;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

/**
 * ASTの操作を生成する
 */
public abstract class OperationGenerator {

  private final double weight;
  protected final ASTAnalyzer astAnalyzer = new ASTAnalyzer();
  protected final AccessibleVariableSearcher variableSearcher = new AccessibleVariableSearcher();

  /**
   * コンストラクタ
   *
   * @param weight 重み
   */
  public OperationGenerator(final double weight) {
    this.weight = weight;
  }

  /**
   * ASTの操作を生成する
   *
   * @param location 操作対象のとなる位置
   * @param reusedNode 再利用されるノード
   * @return 操作
   */
  public abstract Operation generate(final JDTASTLocation location, final ASTNode reusedNode);

  /**
   * その位置で操作を適用して問題ないか
   *
   * @param location 操作の対象となる位置
   * @return その位置で操作を適用して問題ないか
   */
  public abstract boolean canBeApply(final JDTASTLocation location);

  /**
   * 引数で与えた位置で再利用するノードを返す
   *
   * @param location 対象となる位置
   * @return 再利用するノード
   */
  public ASTNode chooseNodeForReuse(final CandidateSelection candidateSelection,
      final ASTLocation location, final Type type) {
    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    final FullyQualifiedName fqn = location.getGeneratedAST()
        .getPrimaryClassName();
    final Scope scope = new Scope(type, fqn);
    final List<Variable> variables = variableSearcher.exec(location);
    final Statement statement = (Statement) jdtastLocation.getNode();

    final Query query = new Query(variables, scope,
        canReuseNonControlStatement(jdtastLocation),
        canReuseBreakStatement(jdtastLocation),
        canReuseReturnStatement(jdtastLocation),
        astAnalyzer.getReturnType(statement),
        canReuseContinueStatement(jdtastLocation));
    return candidateSelection.exec(query);
  }

  /**
   * @param location 対象のノードの位置
   * @return その場所で制御フローに影響を及ばさないステートメントを再利用できるかどうか
   */
  protected boolean canReuseNonControlStatement(final JDTASTLocation location) {
    return true;
  }

  /**
   * @param location 対象のノードの位置
   * @return その場所でbreak文を再利用できるかどうか
   */
  protected boolean canReuseBreakStatement(final JDTASTLocation location) {
    return true;
  }

  /**
   * @param location 対象のノードの位置
   * @return その場所でreturn文を再利用できるかどうか
   */
  protected boolean canReuseReturnStatement(final JDTASTLocation location) {
    return true;
  }

  /**
   * @param location 対象のノードの位置
   * @return その場所でcontinue文を再利用できるかどうか
   */
  protected boolean canReuseContinueStatement(final JDTASTLocation location) {
    return true;
  }

  /**
   * @return その操作を選択する重み
   */
  public double getWeight() {
    return weight;
  }
}
