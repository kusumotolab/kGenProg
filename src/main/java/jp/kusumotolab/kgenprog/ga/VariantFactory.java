package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.Counter;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public interface VariantFactory  {

  Variant exec(final OrdinalNumber generation, final Counter variantCounter, final Gene gene,
      final GeneratedSourceCode sourceCode, final HistoricalElement element,
      final Strategies strategies);
}
