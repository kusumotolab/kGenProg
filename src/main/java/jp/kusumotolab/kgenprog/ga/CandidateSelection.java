package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public interface CandidateSelection {

  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates);

  public Statement exec();
}
