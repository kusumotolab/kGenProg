package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public interface SourceCodeGeneration {

  public void exec(Variant variant, TargetProject targetProject);
}
