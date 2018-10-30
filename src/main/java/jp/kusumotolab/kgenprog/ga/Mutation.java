package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public abstract class Mutation {

  private static Logger log = LoggerFactory.getLogger(Mutation.class);

  protected final Random random;
  protected final int mutationGeneratingCount;
  protected final CandidateSelection candidateSelection;

  public Mutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection) {
    this.random = random;
    this.mutationGeneratingCount = mutationGeneratingCount;
    this.candidateSelection = candidateSelection;
  }

  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    log.debug("enter setCandidates(List<>)");

    candidateSelection.setCandidates(candidates);
    log.debug("exit setCandidates(List<>)");
  }

  public abstract List<Variant> exec(VariantStore variantStore);

}
