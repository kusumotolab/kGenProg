package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinglePointCrossover implements Crossover {

  private static Logger log = LoggerFactory.getLogger(SinglePointCrossover.class);

  private final Random random;
  private final int numberOfPair;

  public SinglePointCrossover(final Random random, final int numberOfPair) {
    this.random = random;
    this.numberOfPair = numberOfPair;
  }

  public SinglePointCrossover(final Random random) {
    this(random, 10);
  }

  @Override
  public List<Gene> exec(final List<Variant> variants) {
    log.debug("enter exec(List<>)");

    final List<Variant> filteredVariants = variants.stream()
        .filter(e -> !e.getGene()
            .getBases()
            .isEmpty())
        .collect(Collectors.toList());

    if (filteredVariants.isEmpty()) {
      return Collections.emptyList();
    }

    return IntStream.range(0, numberOfPair)
        .mapToObj(e -> makeGenes(filteredVariants))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private List<Gene> makeGenes(final List<Variant> variants) {
    final Variant variantA = variants.get(random.nextInt(variants.size()));
    final Variant variantB = variants.get(random.nextInt(variants.size()));
    final Gene geneA = variantA.getGene();
    final Gene geneB = variantB.getGene();
    final List<Base> basesA = geneA.getBases();
    final List<Base> basesB = geneB.getBases();
    final int index = random.nextInt(Math.min(basesA.size(), basesB.size()));
    return Arrays.asList(
        makeGene(basesA.subList(0, index), basesB.subList(index, basesB.size())),
        makeGene(basesB.subList(0, index), basesA.subList(index, basesA.size()))
    );
  }

  private Gene makeGene(final List<Base> basesA, final List<Base> basesB) {
    final ArrayList<Base> bases = new ArrayList<>(basesA);
    bases.addAll(basesB);
    return new SimpleGene(bases);
  }
}
