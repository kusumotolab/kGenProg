package jp.kusumotolab.kgenprog.ga;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  private static Logger log = LoggerFactory.getLogger(DefaultCodeValidation.class);

  private Set<String> sourceCodeSet = new HashSet<>();

  @Override
  public GeneratedSourceCode exec(Gene gene, TargetProject targetProject) {
    log.debug("enter exec(Gene, TargetProject)");

    final Variant initialVariant = targetProject.getInitialVariant();
    GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();
    for (Base base : gene.getBases()) {
      generatedSourceCode = base.getOperation()
          .apply(generatedSourceCode, base.getTargetLocation());
    }

    if (sourceCodeSet.contains(generatedSourceCode.getMessageDigest())) {
      generatedSourceCode = GenerationFailedSourceCode.GENERATION_FAILED;
    } else {
      sourceCodeSet.add(generatedSourceCode.getMessageDigest());
    }

    log.debug("exit exec(Gene, TargetProject)");
    return generatedSourceCode;
  }

}
