package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.Operation;

public interface JDTOperation extends Operation {

  @Override
  default public GeneratedSourceCode apply(final GeneratedSourceCode generatedSourceCode,
      final Location location) {

    try {
      final List<GeneratedAST> newASTs = generatedSourceCode.getFiles()
          .stream()
          .map(ast -> applyEachAST(ast, location))
          .collect(Collectors.toList());
      return new GeneratedSourceCode(newASTs);
    } catch (Exception e) {
      // e.printStackTrace();
      return GenerationFailedSourceCode.GENERATION_FAILED;
    }
  }

  default public GeneratedAST applyEachAST(final GeneratedAST ast, final Location location) {
    if (!ast.getSourceFile()
        .equals(location.getSourceFile())) {
      return ast;
    }

    final GeneratedJDTAST jdtast = (GeneratedJDTAST) ast;
    final ASTRewrite astRewrite = ASTRewrite.create(jdtast.getRoot()
        .getAST());

    applyToASTRewrite((GeneratedJDTAST) ast, (JDTLocation) location, astRewrite);

    final Document document = new Document(jdtast.getSourceCode());
    try {
      astRewrite.rewriteAST(document, null)
          .apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      throw new RuntimeException(e);
    }

    return jdtast.getConstruction()
        .constructAST(ast.getSourceFile(), document.get());
  }

  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTLocation location,
      final ASTRewrite astRewrite);
}
