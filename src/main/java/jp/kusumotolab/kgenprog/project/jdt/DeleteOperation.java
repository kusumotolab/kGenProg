package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class DeleteOperation extends JDTOperation {

  @Override
  protected <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
      final JDTASTLocation location, final ASTRewrite astRewrite) {
    astRewrite.remove(location.locate(ast.getRoot()), null);
  }

  @Override
  public String getName(){
    return "delete";
  }

}
