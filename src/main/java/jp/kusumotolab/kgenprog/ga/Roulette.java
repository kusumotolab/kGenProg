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
  private final List<T> candidateList = new ArrayList<>();
  private final Random random;

  public Roulette(final List<T> candidateList, final Function<T, Double> weightFunction,
      final Random random) {
    if (candidateList.isEmpty()) {
      throw new IllegalArgumentException("candidateList must have at least one element.");
    }
    final List<Double> weightList = candidateList.stream()
        .map(weightFunction)
        .collect(Collectors.toList());
    double total = 0.0d;
    for (Double weight : weightList) {
      total += weight;
      separateList.add(total);
    }
    this.total = total;
    this.candidateList.addAll(candidateList);
    this.random = random;
  }

  public T exec() {
    final double weight = random.nextDouble() * total;
    final int searchResult =
        Collections.binarySearch(separateList, weight, Comparator.naturalOrder());
    final int index = convertToIndex(searchResult);
    return candidateList.get(index);
  }

  public List<T> getCandidateList() {
    return candidateList;
  }

  private int convertToIndex(final int searchResult) {
    if (searchResult < 0) {
      return -(searchResult + 1);
    }
    return searchResult;
  }
}
