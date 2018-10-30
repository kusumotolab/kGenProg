package jp.kusumotolab.kgenprog.ga;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;

public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  private static Logger log = LoggerFactory.getLogger(DefaultCodeValidation.class);

  private final Set<String> sourceCodeSet = new HashSet<>();

  @Override
  public void initialize(final Variant initialVariant) {
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();
    sourceCodeSet.add(generatedSourceCode.getMessageDigest());
  }

  @Override
  public GeneratedSourceCode exec(final VariantStore variantStore, final Gene gene) {
    log.debug("enter exec(Variant, TargetProject)");

    final Variant initialVariant = variantStore.getInitialVariant();
    GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    for (final Base base : gene.getBases()) {
      generatedSourceCode = base.getOperation()
          .apply(generatedSourceCode, base.getTargetLocation());
    }

    if (sourceCodeSet.contains(generatedSourceCode.getMessageDigest())) {
      log.debug("generate duplicate sourcecode from gene " + gene.toString());
      generatedSourceCode = new GenerationFailedSourceCode("duplicate sourcecode");
    } else {
      sourceCodeSet.add(generatedSourceCode.getMessageDigest());
    }

    log.debug("exit exec(Gene, TargetProject)");
    return generatedSourceCode;
  }

}
