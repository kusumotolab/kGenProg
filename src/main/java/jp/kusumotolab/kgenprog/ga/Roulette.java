package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Roulette<T> {

  private final double total;
  private final List<Double> separateList = new ArrayList<>();
  private final List<T> valueList = new ArrayList<>();
  private final Random random;

  public Roulette(final List<T> valueList, final Function<T, Double> weightFunction,
      final Random random) {
    if (valueList.isEmpty()) {
      throw new IllegalArgumentException("valueList must have at least one element.");
    }
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
    this.random = random;
  }

  public T exec() {
    final double weight = random.nextDouble() * total;
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
