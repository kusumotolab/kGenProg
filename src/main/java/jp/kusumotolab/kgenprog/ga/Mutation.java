package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;

public abstract class Mutation {

  private static Logger log = LoggerFactory.getLogger(Mutation.class);

  protected final RandomNumberGeneration randomNumberGeneration;
  protected final int numberOfBase;
  protected final CandidateSelection candidateSelection;

  public Mutation(final int numberOfBase, final RandomNumberGeneration randomNumberGeneration,
      final CandidateSelection candidateSelection) {
    this.randomNumberGeneration = randomNumberGeneration;
    this.numberOfBase = numberOfBase;
    this.candidateSelection = candidateSelection;
  }

  public void setCandidates(final List<GeneratedAST> candidates) {
    log.debug("enter setCandidates(List<>)");

    candidateSelection.setCandidates(candidates);
    log.debug("exit setCandidates(List<>)");
  }

  public abstract List<Base> exec(List<Suspiciousness> suspiciousnesses);

}
