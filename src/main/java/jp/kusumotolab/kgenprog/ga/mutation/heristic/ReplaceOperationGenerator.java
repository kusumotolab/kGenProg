package jp.kusumotolab.kgenprog.ga.mutation.heristic;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class ReplaceOperationGenerator extends OperationGenerator {

  public ReplaceOperationGenerator(final double weight) {
    super(weight);
  }

  @Override
  public Operation generate(final JDTASTLocation location, final ASTNode reusedNode) {
    return new ReplaceOperation(reusedNode);
  }

  @Override
  public boolean enable(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    if (!(node instanceof Statement)) {
      return false;
    }
    final Statement statement = (Statement) node;
    return !(statement instanceof VariableDeclarationStatement);
  }

  @Override
  protected boolean canReuseNonControlStatement(final JDTASTLocation location) {
    final Statement statement = ((Statement) location.getNode());
    return astAnalyzer.isVoidMethod(statement) || !astAnalyzer.isEndStatement(statement);
  }

  @Override
  protected boolean canReuseBreakStatement(final JDTASTLocation location) {
    return astAnalyzer.canBreak(location);
  }

  @Override
  protected boolean canReuseReturnStatement(final JDTASTLocation location) {
    final Statement statement = (Statement) location.getNode();
    final ASTNode parent = statement.getParent();
    if (!(parent instanceof Block)) {
      return true;
    }
    final List statements = ((Block) parent).statements();
    return statements.get(statements.size() - 1)
        .equals(statement);
  }

  @Override
  protected boolean canReuseContinueStatement(final JDTASTLocation location) {
    return astAnalyzer.canContinue(location);
  }
}
