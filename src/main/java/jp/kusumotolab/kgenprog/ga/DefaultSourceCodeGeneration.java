package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  @Override
  public GeneratedSourceCode exec(Gene gene, TargetProject targetProject) {
    final Variant initialVariant = targetProject.getInitialVariant();
    GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();
    for (Base base : gene.getBases()) {
      generatedSourceCode =
          base.getOperation().apply(generatedSourceCode, base.getTargetLocation());
    }
    return generatedSourceCode;
  }

}
