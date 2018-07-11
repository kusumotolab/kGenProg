package jp.kusumotolab.kgenprog.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  private static Logger log = LoggerFactory.getLogger(DefaultCodeValidation.class);

  @Override
  public GeneratedSourceCode exec(Gene gene, TargetProject targetProject) {
    log.debug("enter exec(Gene, TargetProject)");

    final Variant initialVariant = targetProject.getInitialVariant();
    GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();
    for (Base base : gene.getBases()) {
      generatedSourceCode =
          base.getOperation().apply(generatedSourceCode, base.getTargetLocation());
    }
    log.debug("exit exec(Gene, TargetProject)");
    return generatedSourceCode;
  }

}
