package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class GeneratedJDTAST implements GeneratedAST {
  private CompilationUnit root;
  private SourceFile sourceFile;
  private List<Statement> statements;
  private Statement[] lineNumberToStatement;

  @Override
  public String getSourceCode() {
    return root.toString();
  }

  public GeneratedJDTAST(SourceFile sourceFile, CompilationUnit root) {
    this.root = root;
    this.sourceFile = sourceFile;

    StatementListVistor visitor = new StatementListVistor();
    visitor.analyzeStatement(root);
    this.statements = visitor.getStatements();
    this.lineNumberToStatement = visitor.getLineToStatement();
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
  public Location inferLocation(int lineNumber) {
    if (0 <= lineNumber && lineNumber < lineNumberToStatement.length) {
      Statement statement = lineNumberToStatement[lineNumber];

      if (statement != null) {
        return new JDTLocation(this.sourceFile, statement);
      }
    }
    return null;
  }
}
