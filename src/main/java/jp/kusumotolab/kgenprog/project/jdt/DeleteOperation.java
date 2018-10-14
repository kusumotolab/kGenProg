package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class DeleteOperation extends JDTOperation {

  @Override
  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTASTLocation location,
      final ASTRewrite astRewrite) {
    astRewrite.remove(location.locate(ast.getRoot()), null);
  }

}
