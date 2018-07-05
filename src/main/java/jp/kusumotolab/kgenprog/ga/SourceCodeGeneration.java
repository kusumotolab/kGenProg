package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public interface SourceCodeGeneration {

  public GeneratedSourceCode exec(Gene gene, TargetProject targetProject);
}
