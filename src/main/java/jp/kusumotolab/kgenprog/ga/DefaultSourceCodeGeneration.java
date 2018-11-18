package jp.kusumotolab.kgenprog.ga;

import java.util.HashSet;
import java.util.Set;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;

public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  private final Set<String> sourceCodeSet = new HashSet<>();

  @Override
  public void initialize(final Variant initialVariant) {
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();
    sourceCodeSet.add(generatedSourceCode.getMessageDigest());
  }

  @Override
  public GeneratedSourceCode exec(final VariantStore variantStore, final Gene gene) {
    final Variant initialVariant = variantStore.getInitialVariant();
    GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    for (final Base base : gene.getBases()) {
      generatedSourceCode = base.getOperation()
          .apply(generatedSourceCode, base.getTargetLocation());
    }

    if (sourceCodeSet.contains(generatedSourceCode.getMessageDigest())) {
      generatedSourceCode = new GenerationFailedSourceCode("duplicate sourcecode");
    } else {
      sourceCodeSet.add(generatedSourceCode.getMessageDigest());
    }

    return generatedSourceCode;
  }

}
