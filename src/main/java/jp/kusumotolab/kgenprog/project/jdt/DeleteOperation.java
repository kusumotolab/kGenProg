package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ASTLocation;

public class DeleteOperation implements JDTOperation {

  @Override
  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTASTLocation location,
      final ASTRewrite astRewrite) {
    astRewrite.remove(location.locate(ast.getRoot()), null);
  }

  @Override
  public GeneratedSourceCode applyDirectly(final GeneratedSourceCode generatedSourceCode,
      final ASTLocation location) {
    final JDTASTLocation jdtLocation = (JDTASTLocation) location;

    generatedSourceCode.getAsts()
        .stream()
        .filter(ast -> ast.getProductSourcePath()
            .equals(location.getProductSourcePath()))
        .forEach(ast -> {
          if (ast.getProductSourcePath()
              .equals(location.getProductSourcePath())) {
            final CompilationUnit unit = ((GeneratedJDTAST) ast).getRoot();
            final ASTNode target = jdtLocation.locate(unit);
            target.delete();
          }
        });

    return generatedSourceCode;
  }


}
