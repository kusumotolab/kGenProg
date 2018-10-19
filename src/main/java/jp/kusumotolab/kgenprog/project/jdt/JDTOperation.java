package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.SourcePath;

public abstract class JDTOperation implements Operation {

  public static final Logger log = LoggerFactory.getLogger(JDTOperation.class);

  @Override
  public GeneratedSourceCode apply(final GeneratedSourceCode generatedSourceCode,
      final ASTLocation location) {

    try {
      final List<GeneratedAST<ProductSourcePath>> newASTs = generatedSourceCode.getProductAsts()
          .stream()
          .map(ast -> applyEachAST(ast, location))
          .collect(Collectors.toList());
      return new GeneratedSourceCode(newASTs, generatedSourceCode.getTestAsts());
    } catch (final Exception e) {
      log.debug("Opperation failed: {}", e.getMessage());
      log.trace("Trace:", e);
      return createGenerationFailedSourceCode(e);
    }
  }

  private <T extends SourcePath> GeneratedAST<T> applyEachAST(final GeneratedAST<T> ast,
      final ASTLocation location) {
    if (!ast.getSourcePath()
        .equals(location.getSourcePath())) {
      return ast;
    }

    final GeneratedJDTAST<T> jdtast = (GeneratedJDTAST<T>) ast;
    final ASTRewrite astRewrite = ASTRewrite.create(jdtast.getRoot()
        .getAST());

    applyToASTRewrite((GeneratedJDTAST<T>) ast, (JDTASTLocation) location, astRewrite);

    final Document document = new Document(jdtast.getSourceCode());
    try {
      astRewrite.rewriteAST(document, null)
          .apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      throw new RuntimeException(e);
    }

    return jdtast.getConstruction()
        .constructAST(ast.getSourcePath(), document.get());
  }

  protected abstract <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
      final JDTASTLocation location, final ASTRewrite astRewrite);

  private GeneratedSourceCode createGenerationFailedSourceCode(final Exception exception) {
    final Throwable cause;
    if (exception.getCause() != null) {
      cause = exception.getCause();
    } else {
      cause = exception;
    }

    return new GenerationFailedSourceCode(cause.getMessage());
  }
}
