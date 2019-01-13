package jp.kusumotolab.kgenprog.project.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

public class ParallelTestExecutor implements TestExecutor {

  private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
      .availableProcessors());
  private final TestExecutor testExecutor;

  public ParallelTestExecutor(final TestExecutor testExecutor) {
    this.testExecutor = testExecutor;
  }

  @Override
  public TestResults exec(final Variant variant) {
    return testExecutor.exec(variant);
  }

  @Override
  public Single<TestResults> execAsync(final Single<Variant> variantSingle) {
    return variantSingle.subscribeOn(Schedulers.from(executorService))
        .map(testExecutor::exec);
  }

  @Override
  public void initialize() {
    testExecutor.initialize();
  }

  @Override
  public void finish() {
    testExecutor.finish();
  }
}
