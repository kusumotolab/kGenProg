package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jp.kusumotolab.kgenprog.Counter;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class LazyVariantFactory implements VariantFactory {

  @Override
  public Variant exec(final OrdinalNumber generation, final Counter variantCounter, final Gene gene,
      final GeneratedSourceCode sourceCode, final HistoricalElement element,
      final Strategies strategies) {

    final Single<TestResults> resultsSingle = Single.create(
        emitter -> emitter.onSuccess(strategies.execTestExecutor(sourceCode)))
        .subscribeOn(Schedulers.newThread())
        .cast(TestResults.class)
        .cache();

    final Single<Fitness> fitnessSingle = resultsSingle.map(
        v -> strategies.execSourceCodeValidation(sourceCode, v))
        .cache();

    final Single<List<Suspiciousness>> suspiciousnessListSingle = resultsSingle.map(
        v -> strategies.execFaultLocalization(sourceCode, v))
        .cache();

    return new LazyVariant(variantCounter.getAndIncrement(), generation.get(), gene, sourceCode,
        resultsSingle, fitnessSingle, suspiciousnessListSingle,
        element);
  }
}
