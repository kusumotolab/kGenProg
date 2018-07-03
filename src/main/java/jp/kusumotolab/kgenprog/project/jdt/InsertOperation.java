package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class InsertOperation implements JDTOperation {
  private final ASTNode astNode;

  public InsertOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public GeneratedSourceCode apply(final GeneratedSourceCode generatedSourceCode,
      final Location location) {
    final List<GeneratedAST> newASTs = generatedSourceCode.getFiles().stream()
        .map(ast -> applyInsertion(ast, location)).collect(Collectors.toList());

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
          insertNode(target);
        });

    return generatedSourceCode;
  }

  private void insertNode(final ASTNode target) {
    final StructuralPropertyDescriptor locationInParent = target.getLocationInParent();
    if (!locationInParent.isChildListProperty()) {
      throw new RuntimeException("can only insert ASTNode into a list");
    }

    @SuppressWarnings("unchecked")
    final List<ASTNode> siblings =
        (List<ASTNode>) target.getParent().getStructuralProperty(locationInParent);
    final int insertIdx = siblings.indexOf(target) + 1;

    final ASTNode copiedNode = ASTNode.copySubtree(target.getAST(), this.astNode);

    siblings.add(insertIdx, copiedNode);
  }


  private GeneratedAST applyInsertion(final GeneratedAST ast, final Location location) {
    if (!ast.getSourceFile().equals(location.getSourceFile())) {
      return ast;
    }

    final JDTLocation jdtLocation = (JDTLocation) location;
    final GeneratedJDTAST jdtast = (GeneratedJDTAST) ast;
    final CompilationUnit root = jdtast.getRoot();
    final ASTNode insertLocation = jdtLocation.locate(root);
    final StructuralPropertyDescriptor locationInParent = insertLocation.getLocationInParent();
    if (!locationInParent.isChildListProperty()) {
      throw new RuntimeException("can only insert ASTNode into a list");
    }
    final ASTRewrite astRewrite = ASTRewrite.create(root.getAST());
    final ListRewrite listRewrite = astRewrite.getListRewrite(insertLocation.getParent(),
        (ChildListPropertyDescriptor) locationInParent);
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), this.astNode);
    listRewrite.insertAfter(copiedNode, insertLocation, null);

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
