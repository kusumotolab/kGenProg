package jp.kusumotolab.kgenprog.ga;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

public class RandomMutation extends Mutation {

  private static Logger log = LoggerFactory.getLogger(RandomMutation.class);

  public RandomMutation(int numberOfBase) {
    super(numberOfBase);
  }

  public RandomMutation(final int numberOfBase,
      final RandomNumberGeneration randomNumberGeneration) {
    super(numberOfBase, randomNumberGeneration);
  }

  public List<Base> exec(List<Suspiciouseness> suspiciousenesses) {
    log.debug("enter exec(List<>)");

    List<Base> bases = suspiciousenesses.stream()
        .sorted(Comparator.comparingDouble(Suspiciouseness::getValue)
            .reversed())
        .map(this::makeBase)
        .collect(Collectors.toList());

    log.debug("exit exec(List<>)");
    return bases;
  }

  private Base makeBase(Suspiciouseness suspiciouseness) {
    log.debug("enter makeBase(Suspiciouseness)");
    return new Base(suspiciouseness.getLocation(), makeOperationAtRandom());
  }

  private Operation makeOperationAtRandom() {
    log.debug("enter makeOperationAtRandom()");
    final int randomNumber = randomNumberGeneration.getRandomNumber(3);
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
    return candidates.get(randomNumberGeneration.getRandomNumber(candidates.size()));
  }
}
