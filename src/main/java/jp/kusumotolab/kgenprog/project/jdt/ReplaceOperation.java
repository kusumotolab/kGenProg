package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class ReplaceOperation extends JDTOperation {

  private final ASTNode astNode;

  public ReplaceOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  protected <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
      final JDTASTLocation location, final ASTRewrite astRewrite) {
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), astNode);
    astRewrite.replace(location.locate(ast.getRoot()), copiedNode, null);
  }
}
