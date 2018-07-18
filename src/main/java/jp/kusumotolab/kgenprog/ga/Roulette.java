package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Roulette<T> {

  private final double total;
  private final List<Double> separateList = new ArrayList<>();
  private final List<T> valueList = new ArrayList<>();
  private final RandomNumberGeneration randomNumberGeneration;

  public Roulette(final List<T> valueList, final Function<T, Double> weightFunction,
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

  public T exec() {
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
