package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public interface CandidateSelection {

  void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates);

  ASTNode exec(final Scope scope);
}
