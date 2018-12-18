package jp.kusumotolab.kgenprog.ga.variant;

import java.util.List;
import io.reactivex.Single;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class LazyVariant extends Variant {

  private final Single<TestResults> testResultsSingle;
  private final Single<Fitness> fitnessSingle;
  private final Single<List<Suspiciousness>> suspiciousnessListSingle;

  public LazyVariant(final long id, final int generationNumber,
      final Gene gene, final GeneratedSourceCode generatedSourceCode,
      final Single<TestResults> testResultsSingle, final Single<Fitness> fitnessSingle,
      final Single<List<Suspiciousness>> suspiciousnessListSingle,
      final HistoricalElement historicalElement) {
    super(id, generationNumber, gene, generatedSourceCode, null, null, null,
        historicalElement);

    this.testResultsSingle = testResultsSingle;
    this.fitnessSingle = fitnessSingle;
    this.suspiciousnessListSingle = suspiciousnessListSingle;

    this.testResultsSingle.subscribe();
  }

  @Override
  public boolean isCompleted() {
    return fitnessSingle.blockingGet()
        .isMaximum();
  }

  @Override
  public boolean isBuildSucceeded() {
    return EmptyTestResults.class != testResultsSingle.blockingGet().getClass();
  }

  @Override
  public TestResults getTestResults() {
    return this.testResultsSingle.blockingGet();
  }

  @Override
  public Fitness getFitness() {
    return this.fitnessSingle.blockingGet();
  }

  @Override
  public List<Suspiciousness> getSuspiciousnesses() {
    return this.suspiciousnessListSingle.blockingGet();
  }
}
