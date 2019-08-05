package jp.kusumotolab.kgenprog.ga.mutation.heuristic;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;

public class InsertBeforeOperationGenerator extends OperationGenerator {

  public InsertBeforeOperationGenerator(final double weight) {
    super(weight);
  }

  @Override
  public Operation generate(final JDTASTLocation location, final ASTNode reusedNode) {
    return new InsertBeforeOperation(reusedNode);
  }

  @Override
  public boolean enable(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    return node.getParent() instanceof Block;
  }

  @Override
  protected boolean canReuseNonControlStatement(final JDTASTLocation location) {
    return true;
  }

  @Override
  protected boolean canReuseBreakStatement(final JDTASTLocation location) {
    return false;
  }

  @Override
  protected boolean canReuseReturnStatement(final JDTASTLocation location) {
    return false;
  }

  @Override
  protected boolean canReuseContinueStatement(final JDTASTLocation location) {
    return false;
  }
}
