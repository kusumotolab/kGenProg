package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Gene gene = (Gene) o;
    return Objects.equals(bases, gene.bases);
  }

  @Override
  public int hashCode() {
    return 31 + bases.hashCode();
  }
}
