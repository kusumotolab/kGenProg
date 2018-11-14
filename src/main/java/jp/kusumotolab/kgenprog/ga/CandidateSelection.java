package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public interface CandidateSelection<T> {

  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates);

  public T exec();

  public default T exec(final FullyQualifiedName fqn) {
    return exec();
  }
}
