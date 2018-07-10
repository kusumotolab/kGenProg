package jp.kusumotolab.kgenprog.project;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;

public interface ResultOutput {
  public void outputResult(TargetProject targetProject, List<Variant> modified);
}
