package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class JDTASTCrossoverLocation extends JDTASTLocation {

  public JDTASTCrossoverLocation(final JDTASTLocation location) {
    super(location.getSourcePath(), location.getNode(), location.getGeneratedAST());
  }

  public JDTASTCrossoverLocation(final SourcePath sourcePath,
      final ASTNode node, final GeneratedJDTAST<?> generatedAST) {
    super(sourcePath, node, generatedAST);
  }

  @Override
  public ASTNode locate(final ASTNode otherASTRoot) {
    // call super as mock
    return super.locate(otherASTRoot);
  }

}
