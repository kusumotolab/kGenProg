package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

// 使っていないが比較用で置いておく
public class SimpleStatementSelection implements CandidateSelection<Statement> {

  private final Random random;
  private StatementVisitor visitor;

  public SimpleStatementSelection(final Random random) {
    this.random = random;
  }

  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    visitor = new StatementVisitor(candidates);
  }

  @Override
  public Statement exec() {
    final List<Statement> statements = visitor.getStatements();
    final int index = random.nextInt(statements.size());
    return statements.get(index);
  }
}
