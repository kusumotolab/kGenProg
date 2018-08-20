package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class ReplaceOperation implements JDTOperation {

  private final ASTNode astNode;

  public ReplaceOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTASTLocation location,
      final ASTRewrite astRewrite) {
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), astNode);
    astRewrite.replace(location.locate(ast.getRoot()), copiedNode, null);
  }
}
