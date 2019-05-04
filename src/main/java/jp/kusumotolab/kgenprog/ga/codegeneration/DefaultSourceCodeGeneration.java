package jp.kusumotolab.kgenprog.ga.codegeneration;

import java.util.HashMap;
import java.util.Map;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ReproducedSourceCode;

public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  private final Map<String, ReproducedStatus> sourceCodeMap = new HashMap<>();

  @Override
  public void initialize(final Variant initialVariant) {
    final GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();
    putSourceCode(generatedSourceCode);
  }

  @Override
  public GeneratedSourceCode exec(final VariantStore variantStore, final Gene gene) {
    final Variant initialVariant = variantStore.getInitialVariant();
    GeneratedSourceCode generatedSourceCode = initialVariant.getGeneratedSourceCode();

    for (final Base base : gene.getBases()) {
      final Operation operation = base.getOperation();
      generatedSourceCode = operation.apply(generatedSourceCode, base.getTargetLocation());
    }

    if (sourceCodeMap.containsKey((generatedSourceCode.getMessageDigest()))) {
      final ReproducedStatus status = sourceCodeMap.get(generatedSourceCode.getMessageDigest());
      status.incrementCounter();
      generatedSourceCode = new ReproducedSourceCode(status);
    } else {
      putSourceCode(generatedSourceCode);
    }

    return generatedSourceCode;
  }

  private void putSourceCode(final GeneratedSourceCode generatedSourceCode) {
    final ReproducedStatus status = new ReproducedStatus(
        generatedSourceCode.isGenerationSuccess(), generatedSourceCode.getGenerationMessage());
    sourceCodeMap.put(generatedSourceCode.getMessageDigest(), status);
  }
}
