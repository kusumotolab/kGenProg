package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

public class InsertOperation implements JDTOperation {

  private final ASTNode astNode;

  public InsertOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTASTLocation location,
      final ASTRewrite astRewrite) {
    final ASTNode target = location.locate(ast.getRoot());
    final ListRewrite listRewrite = astRewrite.getListRewrite(target.getParent(),
        (ChildListPropertyDescriptor) target.getLocationInParent());
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), this.astNode);
    listRewrite.insertAfter(copiedNode, target, null);
  }

  @Override
  public String getName(){
    return "insert";
  }
}
