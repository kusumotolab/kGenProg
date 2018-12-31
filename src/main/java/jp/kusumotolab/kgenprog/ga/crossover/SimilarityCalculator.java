package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SimilarityCalculator {

  static <T, U> double exec(final T t1, final T t2, final Function<T, Collection<U>> function) {
    final Collection<U> apply1 = function.apply(t1);
    final Collection<U> apply2 = function.apply(t2);

    final Set<U> union = new HashSet<>();
    union.addAll(apply1);
    union.addAll(apply2);

    final Set<U> intersection = new HashSet<>();
    intersection.addAll(apply1);
    intersection.retainAll(apply2);

    return (double) intersection.size() / (double) union.size();
  }
}
