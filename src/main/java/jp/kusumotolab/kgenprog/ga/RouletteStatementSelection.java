package jp.kusumotolab.kgenprog.ga;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.Statement;

public class RouletteStatementSelection implements StatementSelection {

  private List<Statement> candidates;
  private final RandomNumberGeneration randomNumberGeneration;
  private Roulette<Statement> roulette;

  public RouletteStatementSelection(final RandomNumberGeneration randomNumberGeneration) {
    this.randomNumberGeneration = randomNumberGeneration;
  }

  @Override
  public void setCandidates(final List<Statement> candidates) {
    this.candidates = candidates;

    this.candidates.sort(Comparator.comparingInt(statement -> {
      final String string = statement.toString();
      return string.length();
    }));

    final Function<Statement, Double> weightFunction = statement -> {
      final String statementString = statement.toString();
      final int statementLength = statementString.length();

      final double inverse = 1 / ((double) statementLength);
      return Math.pow(inverse, 2);
    };

    roulette = new Roulette<>(this.candidates, weightFunction, randomNumberGeneration);
  }

  @Override
  public Statement exec() {
    return roulette.exec();
  }
}
