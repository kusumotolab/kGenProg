package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import jp.kusumotolab.kgenprog.Counter;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class SequentialVariantFactory implements VariantFactory {

  @Override
  public Variant exec(final OrdinalNumber generation, final Counter variantCounter, final Gene gene,
      final GeneratedSourceCode sourceCode, final HistoricalElement element,
      final Strategies strategies) {
    final TestResults testResults = strategies.execTestExecutor(sourceCode);
    final Fitness fitness = strategies.execSourceCodeValidation(sourceCode, testResults);
    final List<Suspiciousness> suspiciousnesses =
        strategies.execFaultLocalization(sourceCode, testResults);
    return new Variant(variantCounter.getAndIncrement(), generation.get(), gene, sourceCode,
        testResults, fitness, suspiciousnesses,
        element);
  }
}
