package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.eclipse.jdt.core.dom.Statement;

public class SimpleStstementSelection implements StatementSelection {

  private List<Statement> candidates;
  private final RandomNumberGeneration randomNumberGeneration;

  public SimpleStstementSelection(
      RandomNumberGeneration randomNumberGeneration) {
    this.randomNumberGeneration = randomNumberGeneration;
  }

  @Override
  public void setCandidates(List<Statement> candidates) {
    this.candidates = candidates;
  }

  @Override
  public Statement exec() {
    final int index = randomNumberGeneration.getInt(candidates.size());
    return candidates.get(index);
  }
}
