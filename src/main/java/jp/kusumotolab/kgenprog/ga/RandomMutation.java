package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class RandomMutation extends Mutation {

  private static final Logger log = LoggerFactory.getLogger(RandomMutation.class);

  public RandomMutation(final int numberOfBase,
      final RandomNumberGeneration randomNumberGeneration,
      final CandidateSelection candidateSelection) {
    super(numberOfBase, randomNumberGeneration, candidateSelection);
  }

  public List<Base> exec(final List<Suspiciousness> suspiciousnesses) {
    log.debug("enter exec(List<>)");

    final List<Base> bases = new ArrayList<>();
    if (suspiciousnesses.isEmpty()) {
      return bases;
    }

    final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);

    final Roulette<Suspiciousness> roulette =
        new Roulette<>(suspiciousnesses, weightFunction, randomNumberGeneration);

    for (int i = 0; i < numberOfBase; i++) {
      final Suspiciousness suspiciousness = roulette.exec();
      final Base base = makeBase(suspiciousness);
      bases.add(base);
    }

    log.debug("exit exec(List<>)");
    return bases;
  }

  private Base makeBase(final Suspiciousness suspiciousness) {
    log.debug("enter makeBase(Suspiciousness)");
    return new Base(suspiciousness.getLocation(), makeOperationAtRandom());
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

  private ASTNode chooseNodeAtRandom() {
    log.debug("enter chooseNodeAtRandom()");
    return candidateSelection.exec();
  }
}
