package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedAST;

public abstract class Mutation {

  private static Logger log = LoggerFactory.getLogger(Mutation.class);

  protected final Random random;
  protected final int numberOfBase;
  protected final CandidateSelection candidateSelection;

  public Mutation(final int numberOfBase, final Random random,
      final CandidateSelection candidateSelection) {
    this.random = random;
    this.numberOfBase = numberOfBase;
    this.candidateSelection = candidateSelection;
  }

  public void setCandidates(final List<GeneratedAST> candidates) {
    log.debug("enter setCandidates(List<>)");

    candidateSelection.setCandidates(candidates);
    log.debug("exit setCandidates(List<>)");
  }

  public abstract List<Variant> exec(VariantStore variantStore);

}
