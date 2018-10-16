package jp.kusumotolab.kgenprog.ga;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import jp.kusumotolab.kgenprog.Configuration;

public class RouletteTest {

  @Test
  public void testExec() {
    final Random random = new Random(Configuration.DEFAULT_RANDOM_SEED);
    final List<Integer> indexList = IntStream.range(0, 10)
        .boxed()
        .collect(Collectors.toList());
    final Function<Integer, Double> weightFunction = Integer::doubleValue;
    final Roulette<Integer> roulette = new Roulette<>(indexList, weightFunction, random);

    final Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < 1000; i++) {
      final Integer selectedIndex = roulette.exec();
      if (!map.containsKey(selectedIndex)) {
        map.put(selectedIndex, 0);
      }
      final Integer value = map.get(selectedIndex);
      map.put(selectedIndex, value + 1);
    }

    final List<Entry<Integer, Integer>> sortedEntrySet = map.entrySet()
        .stream()
        .sorted(Comparator.comparingInt(Entry::getKey))
        .collect(Collectors.toList());
    assertThat(sortedEntrySet).isSortedAccordingTo(Comparator.comparingInt(Entry::getValue));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testException() {
    final Random random = new Random(Configuration.DEFAULT_RANDOM_SEED);
    new Roulette<>(new ArrayList<>(), Integer::doubleValue, random);
  }
}
