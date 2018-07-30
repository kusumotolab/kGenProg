package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;

public interface CandidateSelection {

  public void setCandidates(final List<GeneratedAST> candidates);
  public Statement exec();
}
