package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class RouletteStatementSelection implements CandidateSelection {

  private final Random random;
  private Roulette<Statement> roulette;

  public RouletteStatementSelection(final Random random) {
    this.random = random;
  }

  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    final StatementVisitor visitor = new StatementVisitor(candidates);

    final Function<Statement, Double> weightFunction = statement -> {
      final int statementWeight = getStatementWeight(statement);

      final double inverse = 1 / ((double) statementWeight);
      return Math.pow(inverse, 2);
    };

    final List<Statement> statements = visitor.getStatements();
    roulette = new Roulette<>(statements, weightFunction, random);
  }

  protected int getStatementWeight(Statement statement) {
    final StatementVisitor statementVisitor = new StatementVisitor(statement);
    final List<Statement> statements = statementVisitor.getStatements();
    return statements.size();
  }

  @Override
  public Statement exec() {
    return roulette.exec();
  }
}
