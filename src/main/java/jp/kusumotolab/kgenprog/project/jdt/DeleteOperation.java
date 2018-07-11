package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class DeleteOperation implements JDTOperation {

  @Override
  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTLocation location,
      final ASTRewrite astRewrite) {
    astRewrite.remove(location.locate(ast.getRoot()), null);
  }

  @Override
  public GeneratedSourceCode applyDirectly(final GeneratedSourceCode generatedSourceCode,
      final Location location) {
    final JDTLocation jdtLocation = (JDTLocation) location;

    generatedSourceCode.getFiles().stream()
        .filter(ast -> ast.getSourceFile().equals(location.getSourceFile())).forEach(ast -> {
          if (ast.getSourceFile().equals(location.getSourceFile())) {
            final CompilationUnit unit = ((GeneratedJDTAST) ast).getRoot();
            final ASTNode target = jdtLocation.locate(unit);
            target.delete();
          }
        });

    return generatedSourceCode;
  }


}
