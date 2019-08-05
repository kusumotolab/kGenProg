package jp.kusumotolab.kgenprog.ga.mutation.heristic;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class InsertAfterOperationGenerator extends OperationGenerator {

  public InsertAfterOperationGenerator(final double weight) {
    super(weight);
  }

  @Override
  public Operation generate(final JDTASTLocation location, final ASTNode reusedNode) {
    return new InsertAfterOperation(reusedNode);
  }

  @Override
  public boolean enable(final JDTASTLocation location) {
    final Statement node = (Statement) location.getNode();
    return node.getParent() instanceof Block && astAnalyzer.canInsertAfter(node);
  }

  @Override
  protected boolean canReuseNonControlStatement(final JDTASTLocation location) {
    final Statement statement = (Statement) location.getNode();
    return astAnalyzer.canInsertAfter(statement);
  }

  @Override
  protected boolean canReuseBreakStatement(final JDTASTLocation location) {
    return astAnalyzer.canBreak(location);
  }

  @Override
  protected boolean canReuseReturnStatement(final JDTASTLocation location) {
    final Statement statement = (Statement) location.getNode();
    return astAnalyzer.canInsertAfter(statement)
        && astAnalyzer.isLastStatement(location);
  }

  @Override
  protected boolean canReuseContinueStatement(final JDTASTLocation location) {
    return astAnalyzer.canContinue(location);
  }
}
