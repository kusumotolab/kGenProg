package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class ReplaceOperation implements JDTOperation {
  private final ASTNode astNode;

  public ReplaceOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public GeneratedSourceCode apply(final GeneratedSourceCode generatedSourceCode,
      final Location location) {
    final List<GeneratedAST> newASTs = generatedSourceCode.getFiles().stream()
        .map(ast -> applyReplaceOperation(ast, location)).collect(Collectors.toList());

    return new GeneratedSourceCode(newASTs);
  }

  @Override
  public GeneratedSourceCode applyDirectly(final GeneratedSourceCode generatedSourceCode,
      final Location location) {
    final JDTLocation jdtLocation = (JDTLocation) location;

    generatedSourceCode.getFiles().stream()
        .filter(ast -> ast.getSourceFile().equals(location.getSourceFile())).forEach(ast -> {
          final CompilationUnit unit = ((GeneratedJDTAST) ast).getRoot();
          final ASTNode target = jdtLocation.locate(unit);

          replaceNode(target);
        });

    return generatedSourceCode;
  }

  private void replaceNode(final ASTNode target) {
    final StructuralPropertyDescriptor locationInParent = target.getLocationInParent();

    final ASTNode copiedNode = ASTNode.copySubtree(target.getAST(), this.astNode);

    if (locationInParent.isChildListProperty()) {
      @SuppressWarnings("unchecked")
      final List<ASTNode> siblings =
          (List<ASTNode>) target.getParent().getStructuralProperty(locationInParent);
      final int replaceIdx = siblings.indexOf(target);
      siblings.set(replaceIdx, copiedNode);

    } else if (locationInParent.isChildProperty()) {
      target.getParent().setStructuralProperty(locationInParent, copiedNode);

    } else {
      throw new RuntimeException("can't replace node");
    }
  }

  private GeneratedAST applyReplaceOperation(final GeneratedAST ast, final Location location) {
    if (!ast.getSourceFile().equals(location.getSourceFile())) {
      return ast;
    }

    final JDTLocation jdtLocation = (JDTLocation) location;
    final GeneratedJDTAST jdtast = (GeneratedJDTAST) ast;
    final CompilationUnit root = jdtast.getRoot();
    final ASTNode replaceNode = jdtLocation.locate(root);
    final ASTRewrite astRewrite = ASTRewrite.create(root.getAST());
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), astNode);

    astRewrite.replace(replaceNode, copiedNode, null);


    final Document document = new Document(jdtast.getSourceCode());
    final TextEdit edit = astRewrite.rewriteAST(document, null);
    try {
      edit.apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      throw new RuntimeException(e);
    }

    return jdtast.getConstruction().constructAST(ast.getSourceFile(), document.get());
  }
}
