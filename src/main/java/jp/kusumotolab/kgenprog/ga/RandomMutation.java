package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
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

  public List<Base> exec(final List<Suspiciouseness> suspiciousenesses) {
    log.debug("enter exec(List<>)");

    final List<Base> bases = new ArrayList<>();
    if (suspiciousenesses.isEmpty())
      return bases;

    final List<Double> keyList = suspiciousenesses.stream()
        .map(e -> Math.pow(e.getValue(), 2))
        .collect(Collectors.toList());
    final Roulette<Suspiciouseness> roulette = new Roulette<>(keyList, suspiciousenesses,
        randomNumberGeneration);

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
    return candidates.get(randomNumberGeneration.getInt(candidates.size()));
  }

  class Roulette<T> {

    private final double total;
    private final List<Double> separateList = new ArrayList<>();
    private final List<T> valueList = new ArrayList<>();
    private final RandomNumberGeneration randomNumberGeneration;

    Roulette(final List<Double> keyList, final List<T> valueList,
        final RandomNumberGeneration randomNumberGeneration) {
      double total = 0.0d;
      for (Double key : keyList) {
        total += key;
        separateList.add(total);
      }
      separateList.add(total);
      this.total = total;
      this.valueList.addAll(valueList);
      this.randomNumberGeneration = randomNumberGeneration;
    }

    T exec() {
      final double key = randomNumberGeneration.getDouble(total);
      for (int i = 0; i < separateList.size() - 1; i++) {
        final Double separate = separateList.get(i);
        if (key < separate) {
          return valueList.get(i);
        }
      }
      return valueList.get(valueList.size() - 1);
    }
  }
}
