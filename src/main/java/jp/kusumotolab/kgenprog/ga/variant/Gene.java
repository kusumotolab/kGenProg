package jp.kusumotolab.kgenprog.ga.variant;

import java.util.ArrayList;
import java.util.List;

public class Gene {

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
