package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.List;
import java.util.Random;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class RouletteStatementSelection extends StatementSelection {

  public RouletteStatementSelection(final Random random) {
    super(random);
  }

  @Override
  public double getStatementWeight(final ReuseCandidate<Statement> reuseCandidate) {
    final Statement statement = reuseCandidate.getValue();
    final FullyQualifiedName fqn = reuseCandidate.getFqn();
    final StatementVisitor statementVisitor = new StatementVisitor(statement, fqn);
    final List<ReuseCandidate<Statement>> statements = statementVisitor.getReuseCandidateList();
    final int size = statements.size();
    return 1 / ((double) size);
  }
}
