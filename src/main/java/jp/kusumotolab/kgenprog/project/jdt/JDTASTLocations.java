package jp.kusumotolab.kgenprog.project.jdt;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class JDTASTLocations<T extends SourcePath> implements ASTLocations {

  private final List<List<ASTNode>> lineNumberToStatements;
  private final List<ASTLocation> allLocations;
  private final T sourcePath;
  private final GeneratedJDTAST<?> generatedAST;

  public JDTASTLocations(final GeneratedJDTAST<?> generatedAST, final CompilationUnit root,
      final T sourcePath) {
    this.generatedAST = generatedAST;
    this.sourcePath = sourcePath;
    final ProgramElementVisitor visitor = new StatementAndConditionVisitor();
    visitor.analyzeElements(root);
    this.lineNumberToStatements = visitor.getLineToElements();
    this.allLocations = visitor.getElements()
        .stream()
        .map(v -> new JDTASTLocation(sourcePath, v, generatedAST))
        .collect(Collectors.toList());
  }

  @Override
  public List<ASTLocation> getAll() {
    return allLocations;
  }

  @Override
  public List<ASTLocation> infer(final int lineNumber) {
    if (0 <= lineNumber && lineNumber < lineNumberToStatements.size()) {
      return lineNumberToStatements.get(lineNumber)
          .stream()
          .map(statement -> new JDTASTLocation(this.sourcePath, statement, generatedAST))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
