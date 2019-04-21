package jp.kusumotolab.kgenprog.ga.codegeneration;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.DuplicatedSourceCode;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public class DefaultSourceCodeGeneration implements SourceCodeGeneration {

  private final Map<String, Pair<Boolean, String>> sourceCodeMap = new HashMap<>();

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
      generatedSourceCode = base.getOperation()
          .apply(generatedSourceCode, base.getTargetLocation());
    }

    if (sourceCodeMap.containsKey((generatedSourceCode.getMessageDigest()))) {
      final Pair<Boolean, String> pair = sourceCodeMap.get(generatedSourceCode.getMessageDigest());
      generatedSourceCode = new DuplicatedSourceCode(pair.getLeft(), pair.getRight());
    } else {
      putSourceCode(generatedSourceCode);
    }

    return generatedSourceCode;
  }

  private void putSourceCode(final GeneratedSourceCode generatedSourceCode) {
    final ImmutablePair<Boolean, String> pair = new ImmutablePair<>(
        generatedSourceCode.isGenerationSuccess(), generatedSourceCode.getGenerationMessage());
    sourceCodeMap.put(generatedSourceCode.getMessageDigest(), pair);
  }
}
