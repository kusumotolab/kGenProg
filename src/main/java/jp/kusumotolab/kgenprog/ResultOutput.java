package jp.kusumotolab.kgenprog;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.TargetProject;

public interface ResultOutput {
  public void outputResult(TargetProject targetProject, List<Variant> modified);
}
