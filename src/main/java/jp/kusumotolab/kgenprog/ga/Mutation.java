package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;

public interface Mutation {

  public List<Base> exec(List<Suspiciouseness> suspiciousenesses);

  public void setCandidates(List<GeneratedAST> candidates);
}
