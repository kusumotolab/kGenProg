package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;

public class SimpleStatementSelection implements CandidateSelection {

  private final RandomNumberGeneration randomNumberGeneration;
  private StatementVisitor visitor;

  public SimpleStatementSelection(
      RandomNumberGeneration randomNumberGeneration) {
    this.randomNumberGeneration = randomNumberGeneration;
  }

  @Override
  public void setCandidates(List<GeneratedAST> candidates) {
    visitor = new StatementVisitor(candidates);
  }

  @Override
  public Statement exec() {
    final List<Statement> statements = visitor.getStatements();
    final int index = randomNumberGeneration.getInt(statements.size());
    return statements.get(index);
  }
}
