package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.Random;
import org.eclipse.jdt.core.dom.Statement;

// 使っていないが比較用で置いておく
public class SimpleStatementSelection extends StatementSelection {

  public SimpleStatementSelection(final Random random) {
    super(random);
  }

  @Override
  public double getStatementWeight(final ReuseCandidate<Statement> reuseCandidate) {
    return 1.0d;
  }
}
