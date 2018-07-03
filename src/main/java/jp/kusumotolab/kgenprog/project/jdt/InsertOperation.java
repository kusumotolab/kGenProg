package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.Optional;
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
  private ASTNode astNode;

  public InsertOperation(ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, Location location) {
    Optional<GeneratedSourceCode> result = this.applyNewapply(generatedSourceCode, location);

    return result.get();
  }

  @Override
  public GeneratedSourceCode applyDirectly(GeneratedSourceCode generatedSourceCode,
      Location location) {
    JDTLocation jdtLocation = (JDTLocation) location;

    generatedSourceCode.getFiles().stream()
        .filter(ast -> ast.getSourceFile().equals(location.getSourceFile())).forEach(ast -> {
          CompilationUnit unit = ((GeneratedJDTAST) ast).getRoot();
          ASTNode target = jdtLocation.locate(unit);
          insertNode(target);
        });

    return generatedSourceCode;
  }

  private void insertNode(ASTNode target) {
    StructuralPropertyDescriptor locationInParent = target.getLocationInParent();
    if (!locationInParent.isChildListProperty()) {
      throw new RuntimeException("can only insert ASTNode into a list");
    }

    List siblings = (List) target.getParent().getStructuralProperty(locationInParent);
    int insertIdx = siblings.indexOf(target) + 1;

    ASTNode copiedNode = ASTNode.copySubtree(target.getAST(), this.astNode);

    siblings.add(insertIdx, copiedNode);
  }

  public Optional<GeneratedSourceCode> applyNewapply(GeneratedSourceCode generatedSourceCode,
      Location location) {
    try {
      final List<GeneratedAST> newASTs = generatedSourceCode.getFiles().stream()
          .map(ast -> applyInsertion2(ast, location)).collect(Collectors.toList());
      return Optional.of(new GeneratedSourceCode(newASTs));
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  private GeneratedAST applyInsertion2(final GeneratedAST ast, final Location location) {
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
    final ListRewrite listRewrite =
        astRewrite.getListRewrite(insertLocation.getParent(), (ChildListPropertyDescriptor) locationInParent);
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), this.astNode);
    listRewrite.insertAfter(copiedNode, insertLocation, null);

    Document document = new Document(jdtast.getSourceCode());
    TextEdit edit = astRewrite.rewriteAST(document, null);
    try {
      edit.apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      throw new RuntimeException(e);
    }

    return GeneratedJDTAST.generateAST(ast.getSourceFile(), document.get());
  }


}
