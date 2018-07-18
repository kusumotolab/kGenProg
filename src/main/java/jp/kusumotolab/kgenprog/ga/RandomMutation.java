package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
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

  public RandomMutation(final int numberOfBase,
      final RandomNumberGeneration randomNumberGeneration) {
    super(numberOfBase, randomNumberGeneration);
  }

  public List<Base> exec(final List<Suspiciouseness> suspiciousenesses) {
    log.debug("enter exec(List<>)");

    final List<Base> bases = new ArrayList<>();
    if (suspiciousenesses.isEmpty()) {
      return bases;
    }

    final Roulette<Suspiciouseness> roulette = new Roulette<>(suspiciousenesses,
        susp -> Math.pow(susp.getValue(), 2), randomNumberGeneration);

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

    Roulette(final List<T> valueList, final Function<T, Double> weightFunction,
        final RandomNumberGeneration randomNumberGeneration) {
      final List<Double> weightList = valueList.stream()
          .map(weightFunction)
          .collect(Collectors.toList());
      double total = 0.0d;
      for (Double weight : weightList) {
        total += weight;
        separateList.add(total);
      }
      this.total = total;
      this.valueList.addAll(valueList);
      this.randomNumberGeneration = randomNumberGeneration;
    }

    T exec() {
      final double weight = randomNumberGeneration.getDouble(total);
      final int searchResult =
          Collections.binarySearch(separateList, weight, Comparator.naturalOrder());
      final int index = convertToIndex(searchResult);
      return valueList.get(index);
    }

    private int convertToIndex(final int searchResult) {
      if (searchResult < 0) {
        return -(searchResult + 1);
      }
      return searchResult;
    }
  }
}
