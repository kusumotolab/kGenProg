package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public interface SourceCodeGeneration {

  public GeneratedSourceCode exec(VariantStore variantStore, Gene gene);
}
