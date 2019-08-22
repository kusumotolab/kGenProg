package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class DeleteOperationGenerator extends OperationGenerator {

  private final ASTAnalyzer astAnalyzer = new ASTAnalyzer();

  /**
   * コンストラクタ
   *
   * @param weight 重み
   */
  public DeleteOperationGenerator(final double weight) {
    super(weight);
  }

  @Override
  public Operation generate(final JDTASTLocation location, final ASTNode reusedNode) {
    return new DeleteOperation();
  }

  @Override
  public boolean enable(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    if (!(node instanceof Statement)) {
      return false;
    }
    final Statement statement = (Statement) node;
    final ASTNode parent = statement.getParent();
    return !(astAnalyzer.isLastStatement(statement) && !astAnalyzer.isVoidMethod(node))
        && !(statement instanceof VariableDeclarationStatement)
        && !(statement instanceof Block)
        && !(parent instanceof TryStatement)
        && !(parent instanceof ForStatement)
        && !(parent instanceof EnhancedForStatement)
        && !(parent instanceof WhileStatement)
        && !((parent instanceof IfStatement)
        && ((IfStatement) parent).getThenStatement()
        .equals(statement));
  }

  @Override
  public ASTNode chooseNodeForReuse(final CandidateSelection candidateSelection,
      final ASTLocation location, final Type type) {
    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    final ASTNode node = jdtastLocation.getNode();
    final AST ast = node.getAST();
    return ast.newEmptyStatement();
  }
}
