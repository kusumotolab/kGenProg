package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import jp.kusumotolab.kgenprog.project.SourcePath;


public class InsertBlockOperation extends JDTOperation {

  @Override
  protected <T extends SourcePath> void applyToASTRewrite(final GeneratedJDTAST<T> ast,
      final JDTASTLocation location, final ASTRewrite astRewrite) {
    ASTStream.stream(ast.getRoot())
        .filter(Statement.class::isInstance)
        .map(Statement.class::cast)
        .forEach(s -> operation(s, astRewrite));;
  }

  @SuppressWarnings("unchecked")
  private void operation(final Statement s, final ASTRewrite astRewrite) {
    final ASTNode parent = s.getParent();
    if (s.getClass() != Block.class && parent.getClass() != Block.class
        && parent instanceof Statement) {
      final AST ast = astRewrite.getAST();
      final Block block = ast.newBlock();
      final ASTNode copiedNode = ASTNode.copySubtree(astRewrite.getAST(), s);
      block.statements()
          .add(copiedNode);
      astRewrite.replace(s, block, null);
    }
  }



}
