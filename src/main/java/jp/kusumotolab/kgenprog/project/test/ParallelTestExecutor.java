package jp.kusumotolab.kgenprog.project.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;

public class ParallelTestExecutor implements TestExecutor {

  private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
      .availableProcessors());
  private final TestExecutor testExecutor;

  public ParallelTestExecutor(final TestExecutor testExecutor) {
    this.testExecutor = testExecutor;
  }

  @Override
  public TestResults exec(final GeneratedSourceCode generatedSourceCode) {
    return testExecutor.exec(generatedSourceCode);
  }

  @Override
  public Single<TestResults> execAsync(final Single<GeneratedSourceCode> generatedSourceCode) {
    return generatedSourceCode.subscribeOn(Schedulers.from(executorService))
        .map(testExecutor::exec);
  }
}
