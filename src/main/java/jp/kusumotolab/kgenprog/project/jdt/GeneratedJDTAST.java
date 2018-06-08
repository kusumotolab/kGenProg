package jp.kusumotolab.kgenprog.project.jdt;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class GeneratedJDTAST implements GeneratedAST {
  private CompilationUnit root;
  private SourceFile sourceFile;
  private List<List<Statement>> lineNumberToStatements;

  @Override
  public String getSourceCode() {
    return root.toString();
  }

  public GeneratedJDTAST(SourceFile sourceFile, CompilationUnit root) {
    this.root = root;
    this.sourceFile = sourceFile;

    StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeStatement(root);
    this.lineNumberToStatements = visitor.getLineToStatements();
  }

  public CompilationUnit getRoot() {
    return root;
  }

  @Override
  public SourceFile getSourceFile() {
    return sourceFile;
  }

  @Override
  public String getPrimaryClassName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Location> inferLocations(int lineNumber) {
    if (0 <= lineNumber && lineNumber < lineNumberToStatements.size()) {
      return lineNumberToStatements.get(lineNumber).stream()
          .map(statement -> new JDTLocation(this.sourceFile, statement))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
