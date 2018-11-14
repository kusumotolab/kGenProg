package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public abstract class Mutation<T> {

  protected final Random random;
  protected final int mutationGeneratingCount;
  protected final CandidateSelection<T> candidateSelection;

  public Mutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection<T> candidateSelection) {
    this.random = random;
    this.mutationGeneratingCount = mutationGeneratingCount;
    this.candidateSelection = candidateSelection;
  }

  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    candidateSelection.setCandidates(candidates);
  }

  public abstract List<Variant> exec(VariantStore variantStore);

}
