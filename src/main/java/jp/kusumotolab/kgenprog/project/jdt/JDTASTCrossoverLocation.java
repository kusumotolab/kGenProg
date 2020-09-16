package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class JDTASTCrossoverLocation extends JDTASTLocation {

  public JDTASTCrossoverLocation(final SourcePath sourcePath,
      final ASTNode node, final GeneratedJDTAST<?> generatedAST) {
    super(sourcePath, node, generatedAST);
  }

  @Override
  public ASTNode locate(final ASTNode otherASTRoot) {
    return null;
  }

}
