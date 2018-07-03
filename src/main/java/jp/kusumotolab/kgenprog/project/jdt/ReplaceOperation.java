package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class ReplaceOperation implements JDTOperation {
  private final ASTNode astNode;

  public ReplaceOperation(final ASTNode astNode) {
    this.astNode = astNode;
  }

  @Override
  public void applyToASTRewrite(final GeneratedJDTAST ast, final JDTLocation location,
      final ASTRewrite astRewrite) {
    final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), astNode);
    astRewrite.replace(location.locate(ast.getRoot()), copiedNode, null);
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
}
