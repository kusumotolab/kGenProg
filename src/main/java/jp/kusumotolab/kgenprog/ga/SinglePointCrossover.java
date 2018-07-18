package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinglePointCrossover implements Crossover {

  private static Logger log = LoggerFactory.getLogger(SinglePointCrossover.class);

  private final RandomNumberGeneration randomNumberGeneration;
  private final int numberOfGene;

  public SinglePointCrossover(
      final RandomNumberGeneration randomNumberGeneration, final int numberOfGene) {
    this.randomNumberGeneration = randomNumberGeneration;
    this.numberOfGene = numberOfGene;
  }

  public SinglePointCrossover(
      final RandomNumberGeneration randomNumberGeneration) {
    this(randomNumberGeneration, 10);
  }

  @Override
  public List<Gene> exec(final List<Variant> variants) {
    log.debug("enter exec(List<>)");

    if (variants.isEmpty()) {
      return Collections.emptyList();
    }

    final List<Variant> filteredVariants = variants.stream()
        .filter(e -> !e.getGene()
            .getBases()
            .isEmpty())
        .collect(Collectors.toList());

    return IntStream.range(0, numberOfGene)
        .mapToObj(e -> makeGene(filteredVariants))
        .collect(Collectors.toList());
  }

  private Gene makeGene(final List<Variant> variants) {
    final Variant variantA = variants.get(randomNumberGeneration.getInt(variants.size()));
    final Variant variantB = variants.get(randomNumberGeneration.getInt(variants.size()));
    final Gene geneA = variantA.getGene();
    final Gene geneB = variantB.getGene();
    final List<Base> basesA = geneA.getBases();
    final List<Base> basesB = geneB.getBases();
    final int index = randomNumberGeneration.getInt(Math.min(basesA.size(), basesB.size()));
    final List<Base> subListA = new ArrayList<>(basesA.subList(0, index));
    final List<Base> subListB = basesB.subList(index, basesB.size());
    subListA.addAll(subListB);
    return new SimpleGene(subListA);
  }

}
