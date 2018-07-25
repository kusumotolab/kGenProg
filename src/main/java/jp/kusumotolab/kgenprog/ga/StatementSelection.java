package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.eclipse.jdt.core.dom.Statement;

public interface StatementSelection {

  public void setCandidates(final List<org.eclipse.jdt.core.dom.Statement> candidates);
  public Statement exec();

}
