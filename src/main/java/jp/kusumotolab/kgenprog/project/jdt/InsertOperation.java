package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class InsertOperation implements JDTOperation {
  private final ASTNode astNode;

  public InsertOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTLocation location,
      final ASTRewrite astRewrite) {
    final ASTNode target = location.locate(ast.getRoot());
    final ListRewrite listRewrite = astRewrite.getListRewrite(target.getParent(),
        (ChildListPropertyDescriptor) target.getLocationInParent());
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), this.astNode);
    listRewrite.insertAfter(copiedNode, target, null);
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
}
