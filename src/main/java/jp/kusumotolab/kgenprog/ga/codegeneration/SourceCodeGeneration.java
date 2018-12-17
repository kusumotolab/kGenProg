package jp.kusumotolab.kgenprog.ga.codegeneration;

import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public interface SourceCodeGeneration {

  public void initialize(Variant initialVariant);

  public GeneratedSourceCode exec(VariantStore variantStore, Gene gene);
}
