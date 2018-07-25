package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
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

  private static final Logger log = LoggerFactory.getLogger(RandomMutation.class);
  private final StatementSelection statementSelection;

  public RandomMutation(final int numberOfBase,
      final RandomNumberGeneration randomNumberGeneration,
      final StatementSelection statementSelection) {
    super(numberOfBase, randomNumberGeneration);
    this.statementSelection = statementSelection;
  }

  @Override
  public void setCandidates(final List<GeneratedAST> candidates) {
    log.debug("enter setCandidates(List<>)");

    super.setCandidates(candidates);
    statementSelection.setCandidates(this.candidates);

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
    return statementSelection.exec();
  }
}
