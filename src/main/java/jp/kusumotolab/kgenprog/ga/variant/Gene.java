package jp.kusumotolab.kgenprog.ga.variant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Gene {

  static public double getSimilarity(final Gene geneA, final Gene geneB) {

    final Set<Base> union = new HashSet<>();
    union.addAll(geneA.getBases());
    union.addAll(geneB.getBases());

    final Set<Base> intersection = new HashSet<>();
    intersection.addAll(geneA.getBases());
    intersection.retainAll(geneB.getBases());

    return (double) intersection.size() / (double) union.size();
  }

  private final List<Base> bases;

  public Gene(final List<Base> bases) {
    this.bases = bases;
  }

  public List<Base> getBases() {
    return bases;
  }

  public List<Gene> generateNextGenerationGenes(final List<Base> bases) {
    final List<Gene> genes = new ArrayList<>();
    for (final Base base : bases) {
      final List<Base> newBases = new ArrayList<>(this.bases);
      newBases.add(base);
      genes.add(new Gene(newBases));
    }
    return genes;
  }
}
