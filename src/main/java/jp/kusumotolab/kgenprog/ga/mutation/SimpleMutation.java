package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

/**
 * 乱数に基づいて変異処理をするクラス
 *
 * @see Mutation
 */
public class SimpleMutation extends Mutation {

  private static final int ATTEMPT_FOR_RESELECTION = 100;

  protected final Type type;

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param type 選択する候補のスコープ
   */
  public SimpleMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final Type type) {
    super(mutationGeneratingCount, random, candidateSelection);
    this.type = type;
  }

  protected Operation makeOperation(final ASTLocation location) {
    final int randomNumber = random.nextInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return random.nextBoolean() ?
            new InsertAfterOperation(chooseNodeForReuse(location))
            : new InsertBeforeOperation(chooseNodeForReuse(location));
      case 2:
        final ASTNode node = chooseNodeForReuse(location);
        return new ReplaceOperation(node);
      default:
        return new NoneOperation();
    }
  }

  /**
   * 再利用するASTノードを選択するメソッド．
   * 再利用先が文の場合は文を，再利用先が式である場合は式を再利用対象として返す．
   *
   * Choosing an AST node for reuse.
   * Chosen node type depends on a given location, which means
   * if a statement is given, a statement is chosen and
   * if an expression is given, an expression is chosen.
   *
   * @param location 再利用するノードで置換されるノード
   * @return 再利用するノード
   */
  protected ASTNode chooseNodeForReuse(final ASTLocation location) {
    final FullyQualifiedName fqn = location.getGeneratedAST()
        .getPrimaryClassName();
    final Scope scope = new Scope(type, fqn);
    final Query query = new Query(scope);

    int attempt = 0;
    final boolean isStatement = location.isStatement();
    final boolean isExpression = location.isExpression();
    while (attempt++ < ATTEMPT_FOR_RESELECTION) {
      final ASTNode nodeForReuse = candidateSelection.exec(query);
      if (isStatement && nodeForReuse instanceof Statement
          || isExpression && nodeForReuse instanceof Expression) {
        return nodeForReuse;
      }
    }
    return null;
  }
}
