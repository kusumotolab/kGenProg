package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class DeleteOperation implements JDTOperation {

  @Override
  public GeneratedSourceCode apply(final GeneratedSourceCode generatedSourceCode,
      final Location location) {
    final List<GeneratedAST> newASTs = generatedSourceCode.getFiles().stream()
        .map(ast -> applyDeleteOperation(ast, location)).collect(Collectors.toList());
    return new GeneratedSourceCode(newASTs);
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

  private GeneratedAST applyDeleteOperation(final GeneratedAST ast, final Location location) {
    if (!ast.getSourceFile().equals(location.getSourceFile())) {
      return ast;
    }

    final JDTLocation jdtLocation = (JDTLocation) location;
    final GeneratedJDTAST jdtast = (GeneratedJDTAST) ast;
    final CompilationUnit root = jdtast.getRoot();
    final ASTNode deleteLocation = jdtLocation.locate(root);

    final ASTRewrite astRewrite = ASTRewrite.create(root.getAST());
    astRewrite.remove(deleteLocation, null);

    final Document document = new Document(jdtast.getSourceCode());
    final TextEdit edit = astRewrite.rewriteAST(document, null);
    try {
      edit.apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      throw new RuntimeException(e);
    }

    return GeneratedJDTAST.generateAST(ast.getSourceFile(), document.get());
  }
}
