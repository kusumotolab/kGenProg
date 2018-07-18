package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class RandomMutation extends Mutation {

  private static Logger log = LoggerFactory.getLogger(RandomMutation.class);
  private Roulette<Statement> statementRoulette;

  public RandomMutation(final int numberOfBase,
      final RandomNumberGeneration randomNumberGeneration) {
    super(numberOfBase, randomNumberGeneration);
  }

  @Override
  public void setCandidates(final List<GeneratedAST> candidates) {
    log.debug("enter setCandidates(List<>)");
    super.setCandidates(candidates);

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

    statementRoulette = new Roulette<>(this.candidates, weightFunction, randomNumberGeneration);

    log.debug("exit setCandidates(List<>)");
  }

  public List<Base> exec(final List<Suspiciouseness> suspiciousenesses) {
    log.debug("enter exec(List<>)");

    final List<Base> bases = new ArrayList<>();
    if (suspiciousenesses.isEmpty()) {
      return bases;
    }

    final Function<Suspiciouseness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);

    final Roulette<Suspiciouseness> roulette =
        new Roulette<>(suspiciousenesses, weightFunction, randomNumberGeneration);

    for (int i = 0; i < numberOfBase; i++) {
      final Suspiciouseness suspiciouseness = roulette.exec();
      final Base base = makeBase(suspiciouseness);
      bases.add(base);
    }

    log.debug("exit exec(List<>)");
    return bases;
  }

  private Base makeBase(Suspiciouseness suspiciouseness) {
    log.debug("enter makeBase(Suspiciouseness)");
    return new Base(suspiciouseness.getLocation(), makeOperationAtRandom());
  }

  private Operation makeOperationAtRandom() {
    log.debug("enter makeOperationAtRandom()");
    final int randomNumber = randomNumberGeneration.getInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return new InsertOperation(chooseNodeAtRandom());
      case 2:
        return new ReplaceOperation(chooseNodeAtRandom());
    }
    return new NoneOperation();
  }

  private Statement chooseNodeAtRandom() {
    log.debug("enter chooseNodeAtRandom()");
    return statementRoulette.exec();
  }
}
