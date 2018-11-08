package jp.kusumotolab.kgenprog.project.jdt;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class JDTASTLocations<T extends SourcePath> implements ASTLocations {

  private final List<List<Statement>> lineNumberToStatements;
  private final List<ASTLocation> allLocations;
  private final T sourcePath;

  public JDTASTLocations(final CompilationUnit root, final T sourcePath) {
    this.sourcePath = sourcePath;
    final StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeStatement(root);
    this.lineNumberToStatements = visitor.getLineToStatements();
    this.allLocations = visitor.getStatements()
        .stream()
        .map(v -> new JDTASTLocation(sourcePath, v))
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
          .map(statement -> new JDTASTLocation(this.sourcePath, statement))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
