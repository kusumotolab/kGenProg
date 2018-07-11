package jp.kusumotolab.kgenprog.project;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public interface ResultOutput {
  public void outputResult(TargetProject targetProject, List<Variant> modified);
}
